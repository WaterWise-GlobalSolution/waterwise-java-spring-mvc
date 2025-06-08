package com.waterwise.service;

import com.waterwise.model.*;
import com.waterwise.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TipoSensorService {

  @Autowired
  TipoSensorRepository tipoSensorRepository;

  public List<TipoSensor> findAll() {
    return tipoSensorRepository.findAll();
  }

}
