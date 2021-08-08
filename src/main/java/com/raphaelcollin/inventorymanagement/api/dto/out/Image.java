package com.raphaelcollin.inventorymanagement.api.dto.out;

import lombok.Value;

@Value
public class Image {
    String identifier;
    String preSignedUrl;

    public static Image fromDomain(final com.raphaelcollin.inventorymanagement.domain.storage.Image image) {
        return new Image(image.getIdentifier(), image.getPreSignedUrl());
    }
}
