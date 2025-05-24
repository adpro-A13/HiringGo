package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.filter.LowonganFilterStrategy;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class LowonganFilterService {

    private final Map<String, LowonganFilterStrategy> strategyMap;

    @Autowired
    public LowonganFilterService(List<LowonganFilterStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        LowonganFilterStrategy::getStrategyName,
                        strategy -> strategy
                ));
    }

    public List<Lowongan> filter(List<Lowongan> lowonganList, String strategyName, String filterValue) {
        LowonganFilterStrategy strategy = strategyMap.get(strategyName);
        if (strategy == null) return lowonganList;
        return strategy.filter(lowonganList, filterValue);
    }
}
