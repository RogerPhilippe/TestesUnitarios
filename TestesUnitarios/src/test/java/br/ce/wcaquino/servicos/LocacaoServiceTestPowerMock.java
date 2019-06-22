package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.matchers.DiaSemanaMatchers;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.*;
import static br.ce.wcaquino.utils.DataUtils.*;
import static java.util.Arrays.asList;
import static java.util.Calendar.MONDAY;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class, DataUtils.class})
public class LocacaoServiceTestPowerMock {

    @InjectMocks
    private LocacaoService locacaoService;

    @Mock
    private SPCService spc;

    @Mock
    private LocacaoDAO dao;

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
        locacaoService = PowerMockito.spy(locacaoService);
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

    @Test
    public void deveDevolverSegundaQuandoAlugadoSabado() throws Exception {

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = asList(umFilme().agora());

        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(obterData(22, 6, 2019));

        /**
         * Para usar com Calendar.getInstance().getTime();
         *
         * Cenário
         *
         Calendar calendar = Calendar.getInstance();
         calendar.set(Calendar.DAY_OF_WEEK, 22);
         calendar.set(MONDAY, Calendar.JUNE);
         calendar.set(Calendar.YEAR, 2019);
         PowerMockito.mockStatic(Calculadora.class);
         PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);
         * Verificação

         PowerMockito.verifyStatic();
         Calendar.getInstance();
         */

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
    public void deveAlugarFilmeSemCalcularValor() throws Exception {

        // Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        PowerMockito.doReturn(1.0).when(locacaoService, "getCalcularValorLocacao", filmes);

        // Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

        // Verificação
        Assert.assertThat(locacao.getValor(), is(1.0));
        PowerMockito.verifyPrivate(locacaoService).invoke("getCalcularValorLocacao", filmes);

    }

    @Test
    public void deveCalcularValorLocacao() throws Exception {

        // Cenário
        List<Filme> filmes = Arrays.asList(umFilme().agora());

        // Ação
        Double valor = Whitebox.invokeMethod(locacaoService, "getCalcularValorLocacao", filmes);

        // Verificação
        Assert.assertThat(valor, is(4.0));

    }

}

