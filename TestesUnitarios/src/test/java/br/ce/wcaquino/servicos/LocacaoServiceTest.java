package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.DiaSemanaMatchers;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.*;
import static br.ce.wcaquino.utils.DataUtils.*;
import static java.util.Arrays.asList;
import static java.util.Calendar.MONDAY;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class, DataUtils.class})
public class LocacaoServiceTest {

    @InjectMocks
    private LocacaoService locacaoService;

    @Mock
    private SPCService spc;

    @Mock
    private LocacaoDAO dao;

    @Mock
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
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void deveAlugarFilme() throws Exception {

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes =
                asList(umFilme().comValor(5.0).agora());

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(obterData(21, 6, 2019));

        // Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

        // Verificação
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(locacao.getValor(), is(not(5.01)));
        error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));

        error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));

        error.checkThat(locacao.getDataLocacao(), ehHoje());
        error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));

        error.checkThat(isMesmaData(locacao.getDataLocacao(), obterData(21, 6, 2019)), is(true));
        error.checkThat(isMesmaData(locacao.getDataRetorno(), obterData(22, 6, 2019)), is(true));

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
    public void deveDevolverSegundaQuandoAlugadoSabado() throws Exception {

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = asList(umFilme().agora());

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(obterData(22, 6, 2019));

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
    public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {

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
    @Test
    public void deveTratarErroSPC() throws Exception {

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        when(spc.possuiNegativavao(usuario)).thenThrow(new Exception("Falha catastrófica"));

        // Verificação
        exception.expect(LocadoraException.class);
        exception.expectMessage("Problema com SPC, tente novamente");

        // Ação
        locacaoService.alugarFilme(usuario, filmes);

    }

    @Test
    public void deveProrrogarUmaLocacao() {

        // Cenário
        Locacao locacao = umLocacao().agora();

        // Ação
        locacaoService.prorrogarLocacao(locacao, 3);

        // Verificação
        ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
        Mockito.verify(dao).salvar(argumentCaptor.capture());
        Locacao locacaoRetorno = argumentCaptor.getValue();

        assertThat(locacaoRetorno.getValor(), is(12.0));
        assertThat(locacaoRetorno.getDataLocacao(), ehHoje());
        assertThat(locacaoRetorno.getDataRetorno(), ehHojeComDiferencaDias(3));

    }

}

