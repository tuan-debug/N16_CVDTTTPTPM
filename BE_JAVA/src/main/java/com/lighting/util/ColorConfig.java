package com.lighting.util;

import lombok.Getter;

@Getter
public class ColorConfig {
    private final String name;

    public ColorConfig(String name) {
        this.name = name;
    }
}