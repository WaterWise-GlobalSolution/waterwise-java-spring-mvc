package com.waterwise.service;

import com.waterwise.model.SensorIoT;
import com.waterwise.repository.SensorIoTRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SensorIoTService {

    @Autowired
    private SensorIoTRepository sensorRepository;

    // ✅ SUBSTITUIR O MÉTODO EXISTENTE OU ADICIONAR SE NÃO EXISTIR
    public List<SensorIoT> findAll() {
        return sensorRepository.findAllWithRelacionamentos();
    }

    // ✅ OPCIONAL: Método específico também
    public List<SensorIoT> findAllWithRelacionamentos() {
        return sensorRepository.findAllWithRelacionamentos();
    }
}
