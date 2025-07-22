package org.example.fanzip.market.service;

import org.example.fanzip.market.domain.MarketVO;
import org.example.fanzip.market.mapper.MarketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketServiceImpl implements MarketService {

    private final MarketMapper marketMapper;

    @Autowired
    public MarketServiceImpl(MarketMapper marketMapper) {
        this.marketMapper = marketMapper;
    }

    @Override
    public List<MarketVO> getAllProducts(int limit) {
        return marketMapper.getAllProducts(limit);
    }

    @Override
    public List<MarketVO> getProductsAfter(Long lastProductId, int limit) {
        return (lastProductId == null)
                ? marketMapper.getAllProducts(limit)
                : marketMapper.getProductsAfter(lastProductId, limit);
    }
}
