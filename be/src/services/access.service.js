import AccountModel from "../models/account.model.js";
import accessHelper from "../helpers/access.js";
import validator from "email-validator";
import bcrypt from 'bcryptjs';
import { getInfoData } from "../utils/index.js";
import redisClient from "../dbs/redisdb.js";
import jwt from "jsonwebtoken"; 
import sendEmail from "../helpers/sendEmail.js";
import dotenv from 'dotenv';
import RedisKeys from "../utils/redisKeys.js";
dotenv.config();

const getRandomSixDigit = () => {
    return Math.floor(Math.random() * (999999 - 100000 + 1)) + 100000;
}

export default class accessService {
    static signUp = async (payload) => {
        const { email, phone } = payload;

        if (!validator.validate(email)) {
            throw new Error('Invalid email');
        }

        const validEmail = await AccountModel.findOne({ email }).lean();
        if (validEmail) {
            throw new Error('Email already exists');
        }

        const validUserPhone = await AccountModel.findOne({ phone }).lean();
        if (validUserPhone) {
            throw new Error('Phone already exists');
        }

        const code = getRandomSixDigit();
        const subject = 'Email verification';
        const text = `Your code is: ${code}`;
        const info = await sendEmail(subject, text, email);
        redisClient.set(RedisKeys.userKey.pinVerify(email), code, 'EX', 60);
        return info;

    };

    static verifyEmail = async (payload) => {
        const { code, ...account } = payload;
        
        const codeToVerify = await redisClient.get(RedisKeys.userKey.pinVerify(account.email));
        console.log(`codeToVerify: ${codeToVerify}`);
        console.log(`code: ${code}`);
        console.log(code === codeToVerify);
        if (codeToVerify !== code) {
            throw new Error('Invalid code');
        }
        await redisClient.del(RedisKeys.userKey.pinVerify(account.email));

        console.log(1);
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(account.password, salt);
        const { password, ...info } = account;
        console.log(`info: ${JSON.stringify(info)}`);
        const verifyAccount = new AccountModel({
            ...info,
            password: hashedPassword,
        });

        console.log(`verifyAccount: ${JSON.stringify(verifyAccount)}`);

        await verifyAccount.save(); 
        return verifyAccount;
    }

    static signIn = async (payload) => {
        const { email, password } = payload;

        const account = await AccountModel.findOne({ email }).lean();
        if (!account) {
            throw new Error('Invalid username or password');
        }

        const isMatch = await bcrypt.compare(password, account.password); 
        if (!isMatch) {
            throw new Error('Invalid username or password');
        }

        const fields = ['_id', 'user_name', 'email', 'role', 'isPremium', 'premiumExpired'];
        const acc = getInfoData({ fields, object: account });
        console.log(acc);

        const accessToken = accessHelper.generateToken(account, process.env.JWT_ACCESS_TOKEN, '2h');
        const refreshToken = accessHelper.generateToken(account, process.env.JWT_REFRESH_TOKEN, '7d');
        
        const redisRefreshKey = RedisKeys.userKey.refreshToken(account._id);

        await redisClient.del(redisRefreshKey);

        redisClient.set(redisRefreshKey, refreshToken);

        return { acc, accessToken, refreshToken };
    };

    static refresh = async (payload) => {
        const { refreshToken } = payload;
        console.log(`refreshToken: ${refreshToken}`);
    
        if (!refreshToken) {
            throw new Error('Unauthorized');
        }
    
        let decoded;
        try {
            decoded = jwt.verify(refreshToken, process.env.JWT_REFRESH_TOKEN);
        } catch (err) {
            if (err.name === 'TokenExpiredError') {
                throw new Error('Refresh token expired');
            }
            throw new Error('Invalid refresh token');
        }
    
        console.log(`Token expires at: ${new Date(decoded.exp * 1000).toISOString()}`);
        console.log(`Current time: ${new Date().toISOString()}`);
    
        const account = await AccountModel.findById(decoded._id).select('_id user_name email role').lean();
        if (!account) {
            throw new Error('User not found');
        }
    
        console.log(`account: ${JSON.stringify(account)}`);
    
        const redisRefreshKey = RedisKeys.userKey.refreshToken(account._id);
        const storedRefreshToken = await redisClient.get(redisRefreshKey);
        
        if (!storedRefreshToken || storedRefreshToken !== refreshToken) {
            throw new Error('Invalid refresh token');
        }
    
        const newAccessToken = accessHelper.generateToken(account, process.env.JWT_ACCESS_TOKEN, '2h');
        const newRefreshToken = accessHelper.generateToken(account, process.env.JWT_REFRESH_TOKEN, '7d');
    
        await redisClient.set(redisRefreshKey, newRefreshToken, 'EX', 7 * 24 * 60 * 60);
    
        return { newAccessToken, newRefreshToken };
    }
    
    static signOut = async (payload) => {
        const { refreshToken, account } = payload;
    
        if (!refreshToken) {
            throw new Error('Unauthorized');
        }
        console.log(process.env.JWT_REFRESH_TOKEN);
    
        const decoded = jwt.verify(refreshToken, process.env.JWT_REFRESH_TOKEN, (err, decoded) => {
            if (err) {
                throw new Error('Invalid refresh token');
            }
            return decoded;
        });
    
        if (decoded._id !== account._id) {
            throw new Error('Unauthorized');
        }
    
        const redisRefreshKey = RedisKeys.userKey.refreshToken(account._id);
        
        const storedRefreshToken = await redisClient.get(redisRefreshKey);
        if (storedRefreshToken !== refreshToken) {
            throw new Error('Invalid refresh token');
        }
    
        await redisClient.del(redisRefreshKey);
    }

    static async resetPassword(account) {
        const { email } = account;
        const code = getRandomSixDigit();
        const subject = 'Reset password';
        const text = `Your code is: ${code}`;
        const info = await sendEmail(subject, text, email);
        redisClient.set(RedisKeys.userKey.pinVerify(email), code, 'EX', 60);
        return info;
    }

    static async resentPin(account) {
        const { email } = account;
        const code = getRandomSixDigit();
        const subject = 'Resent pin';
        const text = `Your code is: ${code}`;
        const info = await sendEmail(subject, text, email);
        redisClient.set(RedisKeys.userKey.pinVerify(email), code, 'EX', 60);
        return info;
    }
    
    static verifyPin = async (payload) => {
        const { email, code } = payload;
        const codeToVerify = await redisClient.get(RedisKeys.userKey.pinVerify(email));
        if(!codeToVerify) {
            throw new Error('Code expired');
        }

        if (codeToVerify !== code) {
            throw new Error('Invalid code');
        }
        await redisClient.del(RedisKeys.userKey.pinVerify(email));
        
        return {
            message: 'Verify pin successfully'
        };
    }

    static forgotPassWord = async (payload) => {
        const { email, newPassword } = payload;
        const account = await AccountModel
        .findOne({ email })
        .select('_id');
        if (!account) {
            throw new Error('Email not found');
        }
        const salt = await bcrypt.genSalt(10);
        account.password = await bcrypt.hash(newPassword, salt);
        await account.save();
    }
}