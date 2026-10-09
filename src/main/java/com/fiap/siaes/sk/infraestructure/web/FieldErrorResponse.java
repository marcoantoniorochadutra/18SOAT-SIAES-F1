package com.fiap.siaes.sk.infraestructure.web;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Builder
public class FieldErrorResponse {

    private String code;
    private String message;
    private String field;

}
