package com.hmaar.sundhed.controller;

import com.hmaar.sundhed.model.interfaces.EKGData;
import com.hmaar.sundhed.model.interfaces.PulsData;
import com.hmaar.sundhed.model.interfaces.TempData;
import com.hmaar.sundhed.model.interfaces.SpO2Data;

// @Author Alaa Mahdi

// Det her er vores listener interface
public interface Observer {
    void update(EKGData ekgData, PulsData pulsData, TempData tempData, SpO2Data spO2Data);
}
