package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.DiaSemanaMatchers;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.*;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static java.util.Arrays.asList;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.*;

public class LocacaoServiceTest {

    private LocacaoService locacaoService;

    private SPCService spc;

    private LocacaoDAO dao;

    private EmailsService emailsService;

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
        dao = Mockito.mock(LocacaoDAO.class);
        locacaoService.setLocacaoDAO(dao);
        spc = Mockito.mock(SPCService.class);
        locacaoService.setSPCService(spc);
        emailsService = Mockito.mock(EmailsService.class);
        locacaoService.setEmailService(emailsService);
    }

    @Test
    public void deveAlugarFilme() throws Exception {

        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date() , SATURDAY));

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes =
                asList(umFilme().comValor(5.0).agora());

        // Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

        // Verificação
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(locacao.getValor(), is(not(5.01)));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));

        error.checkThat(locacao.getDataLocacao(), ehHoje());
        error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));

    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws FilmeSemEstoqueException, LocadoraException {
        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = asList(umFilme().semEstoque().agora());

        // Ação
        locacaoService.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
        // Cenário
        List<Filme> filmes = asList(umFilme().agora());

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
        Usuario usuario = umUsuario().agora();

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme não preenchido");

        //acao
        locacaoService.alugarFilme(usuario, null);
    }

    @Test
    public void deveDevolverSegundaQuandoAlugadoSabado() throws FilmeSemEstoqueException, LocadoraException {

        assumeTrue(DataUtils.verificarDiaSemana(new Date() , SATURDAY));

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = asList(umFilme().agora());

        // Ação
        Locacao retorno = locacaoService.alugarFilme(usuario, filmes);

        // Verificacao
        boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), MONDAY);
        assertTrue(ehSegunda);

        assertThat(retorno.getDataRetorno(), new DiaSemanaMatchers(MONDAY));
        assertThat(retorno.getDataRetorno(), caiEm(MONDAY));
        assertThat(retorno.getDataRetorno(), caiNumaSegunda());

    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() throws FilmeSemEstoqueException {

        //Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = asList(umFilme().agora());

        when(spc.possuiNegativavao(Mockito.any(Usuario.class))).thenReturn(true);

        //Ação
        try {
            locacaoService.alugarFilme(usuario, filmes);
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuário Negativado"));
        }

        // Verificação
        verify(spc).possuiNegativavao(usuario);

    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas() {

        // Cenário
        Usuario usuario = umUsuario().agora();
        Usuario usuarioEmDia = umUsuario().comNome("Usuário em Dia").agora();
        List<Locacao> locacoes = asList(
                umLocacao().atrasado().comUsuario(usuario).comDataLocacao(obterDataComDiferencaDias(-2)).agora(),
                umLocacao().comUsuario(usuarioEmDia).agora()
        );
        when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        // Ação
        locacaoService.notificarAtrasos();

        // Verificação
        verify(emailsService).notificarAtraso(usuario);
        verify(emailsService, never()).notificarAtraso(usuarioEmDia);
        verifyNoMoreInteractions(emailsService);
        Mockito.verifyZeroInteractions(spc);
    }

}
