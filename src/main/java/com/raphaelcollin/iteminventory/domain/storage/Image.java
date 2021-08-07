package com.raphaelcollin.iteminventory.domain.storage;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder(toBuilder = true)
public class Image {
    String identifier;

    @With
    String preSignedUrl;
}
