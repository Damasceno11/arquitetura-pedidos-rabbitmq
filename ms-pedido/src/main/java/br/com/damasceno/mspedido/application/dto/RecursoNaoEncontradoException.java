package br.com.damasceno.mspedido.application.dto;

public class RecursoNaoEncontradoException extends RuntimeException{
    public RecursoNaoEncontradoException(String message){
        super(message);
    }
}
