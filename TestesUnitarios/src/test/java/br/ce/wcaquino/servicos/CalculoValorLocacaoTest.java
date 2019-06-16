package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

    private LocacaoService service;

    @Parameter
    public List<Filme> filmes;

    @Parameter(value = 1)
    public Double valorLocacao;

    @Parameter(value = 2)
    public String cenario;

    private static Filme filme1 = new Filme("Filme 1", 2, 4.0);
    private static Filme filme2 = new Filme("Filme 2", 2, 4.0);
    private static Filme filme3 = new Filme("Filme 3", 2, 4.0);
    private static Filme filme4 = new Filme("Filme 4", 2, 4.0);
    private static Filme filme5 = new Filme("Filme 5", 2, 4.0);
    private static Filme filme6 = new Filme("Filme 6", 2, 4.0);
    private static Filme filme7 = new Filme("Filme 7", 2, 4.0);

    @Before
    public void setup() {
        service = new LocacaoService();
    }

    @Parameters(name = "{2}")
    public static Collection<Object[]> getParametros() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList(filme1), 4d, "1 Filme"},
                {Arrays.asList(filme1, filme2), 8d, "2 Filmes"},
                {Arrays.asList(filme1, filme2, filme3), 11d, "3 Filmes"},
                {Arrays.asList(filme1, filme2, filme3, filme4), 13d, "4 Filmes"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14d, "5 Filmes"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14d, "6 Filmes"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18d, "7 Filmes"}
        });
    }

    @Test
    public void deveCalcularValorLocacao() throws FilmeSemEstoqueException, LocadoraException {
        // Cenário
        Usuario usuario = new Usuario("Usuário 1");

        // Ação
        Locacao resultado = service.alugarFilme(usuario, filmes);

        // Verificacao
        assertEquals(valorLocacao, resultado.getValor(), 0.01);

    }

}
