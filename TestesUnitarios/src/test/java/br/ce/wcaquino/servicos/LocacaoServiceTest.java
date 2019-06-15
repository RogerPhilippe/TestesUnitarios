package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.Date;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

public class LocacaoServiceTest {

    private LocacaoService locacaoService;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    /**
     * Roda ante dos testes da classe, e after roda depois.
     * Existe beroreClass e afterClass que são executados antes da instanciação da classe, e depois do seu fim
     */
    @Before
    public void setup() {
        locacaoService = new LocacaoService();
    }

    @Test
    public void testeLocacao() throws Exception {
        // Cenário
        Usuario usuario = new Usuario("Usuário 1");
        Filme filme = new Filme("Filme 1", 2, 5.0);

        // Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filme);

            // Verificação
            error.checkThat(locacao.getValor(), is(equalTo(5.0)));
            error.checkThat(locacao.getValor(), is(not(5.01)));
            error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
            error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));

    }

}
