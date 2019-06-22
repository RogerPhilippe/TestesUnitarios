package br.ce.wcaquino.servicos;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Roda os testes em ordem alfabética pelos nomes dos métodos
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrdemTest {

    @Test
    public void testeA() {
        //System.out.println("Método A");
    }

    @Test
    public void testeB() {
        //System.out.println("Método B");
    }

}
