package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class LocacaoServiceTest {

    private LocacaoService locacaoService;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    /**
     * Depreciado
     */
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Roda ante dos testes da classe, e after roda depois.
     * Existe beroreClass e afterClass que são executados antes da instanciação da classe, e depois do seu fim
     */
    @Before
    public void setup() {
        locacaoService = new LocacaoService();
    }

    @Test
    public void deveAlugarFilme() throws Exception {

        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date() ,Calendar.SATURDAY));

        // Cenário
        Usuario usuario = new Usuario("Usuário 1");
        List<Filme> filmes =
                Arrays.asList(
                        new Filme("Filme 1", 2, 5.0),
                        new Filme("Filme 2", 3, 4.5));

        // Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

        // Verificação
        error.checkThat(locacao.getValor(), is(equalTo(9.5)));
        error.checkThat(locacao.getValor(), is(not(5.01)));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));

    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws FilmeSemEstoqueException, LocadoraException {
        // Cenário
        Usuario usuario = new Usuario("Usuário 2");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 5.0));

        // Ação
        locacaoService.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
        // Cenário
        List<Filme> filmes = Arrays.asList(new Filme("Filme 2", 1, 5.5));

        // Ação
        try {
            locacaoService.alugarFilme(null, filmes);
            fail();
        } catch (LocadoraException ex) {
            assertEquals(ex.getMessage(), "Usuário não preenchido");
        }
    }

    @Test
    public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
        // Cenário
        Usuario usuario = new Usuario("Usuário 3");

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme não preenchido");

        //acao
        locacaoService.alugarFilme(usuario, null);
    }

    @Test
    public void deveDevolverSegundaQuandoAlugadoSabado() throws FilmeSemEstoqueException, LocadoraException {

        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date() , Calendar.SATURDAY));

        // Cenário
        Usuario usuario = new Usuario("Usuário 4");
        List<Filme> filmes = Arrays.asList(
                new Filme("Filme 4", 1, 5.0)
        );

        // Ação
        Locacao retorno = locacaoService.alugarFilme(usuario, filmes);

        // Verificacao
        boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
        assertTrue(ehSegunda);

    }

}
