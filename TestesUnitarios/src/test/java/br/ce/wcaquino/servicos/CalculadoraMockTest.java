package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class CalculadoraMockTest {

    @Test
    public void test2() {
        Calculadora calculadora = Mockito.mock(Calculadora.class);
        Mockito.when(calculadora.somar(1, 2)).thenReturn(3);

        Assert.assertEquals(3, calculadora.somar(1, 2));
    }

    @Test
    public void testB() {
        Calculadora calculadora = Mockito.mock(Calculadora.class);
        Mockito.when(calculadora.somar(Mockito.anyInt(), Mockito.anyInt())).thenReturn(3);

        Assert.assertEquals(3, calculadora.somar(1, 2));
    }

    @Test
    public void testC() {
        Calculadora calculadora = Mockito.mock(Calculadora.class);
        Mockito.when(calculadora.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(3);

        Assert.assertEquals(3, calculadora.somar(1, 2));
    }

    @Test
    public void testD() {
        Calculadora calculadora = Mockito.mock(Calculadora.class);

        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);

        Mockito.when(calculadora.somar(argumentCaptor.capture(), argumentCaptor.capture())).thenReturn(3);

        Assert.assertEquals(3, calculadora.somar(1, 2));

        //System.out.println(argumentCaptor.getAllValues());

    }

}
