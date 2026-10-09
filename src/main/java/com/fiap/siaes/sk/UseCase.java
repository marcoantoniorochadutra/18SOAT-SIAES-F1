package com.fiap.siaes.sk;

public interface UseCase<I, O> {

    O execute(I input);
}
