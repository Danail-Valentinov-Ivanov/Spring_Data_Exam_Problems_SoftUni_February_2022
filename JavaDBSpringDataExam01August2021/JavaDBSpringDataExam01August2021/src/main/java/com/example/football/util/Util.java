package com.example.football.util;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Util {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
//    TODO:
}
