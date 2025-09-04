package org.example.fanzip.market.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductCreatedEvent {
    private final long productId;
    private final long influencerId;
    private final String productName;
    private final String thumbnailImage;
}
