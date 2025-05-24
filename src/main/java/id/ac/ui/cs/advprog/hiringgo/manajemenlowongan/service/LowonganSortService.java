package id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.service;

import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.sort.LowonganSortStrategy;
import id.ac.ui.cs.advprog.hiringgo.manajemenlowongan.model.Lowongan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LowonganSortService {

    private final Map<String, LowonganSortStrategy> strategyMap;

    @Autowired
    public LowonganSortService(List<LowonganSortStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getClass().getSimpleName(),
                        strategy -> strategy
                ));
    }

    public List<Lowongan> sort(List<Lowongan> lowonganList, String strategyName) {
        LowonganSortStrategy strategy = strategyMap.get(strategyName);
        if (strategy == null) return lowonganList;
        return strategy.sort(lowonganList);
    }
}