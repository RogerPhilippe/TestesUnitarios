package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;
import br.ce.wcaquino.runners.ParallelRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * São criados os métodos, cenários, ações e verificações antes das classes e objetos e entidades etc...
 */
@RunWith(ParallelRunner.class)
public class CalculadoraTest {

    private Calculadora calc;

    @Before
    public void setup() {
        calc = new Calculadora();
    }

    @Test
    public void deveSomarDoisValores() {

        //Cenário
        int a = 5;
        int b = 3;

        // Ação
        int result = calc.somar(a, b);

        // Verificação
        assertEquals(8, result);

    }

    @Test
    public void deveSubtrairDoisValores() {

        // Cenário
        int a = 6;
        int b = 2;

        // Ação
        int result = calc.subtrair(a, b);

        // Verificação
        assertEquals(4, result);

    }

    @Test
    public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {

        // Cenário
        int a = 6;
        int b = 2;

        // Ação
        int result = calc.dividir(a, b);

        // Verificação
        assertEquals(3, result);

    }

    @Test(expected = NaoPodeDividirPorZeroException.class)
    public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {

        // Cenário
        int a = 10;
        int b = 0;

        // Ação
        calc.dividir(a, b);

    }

}
