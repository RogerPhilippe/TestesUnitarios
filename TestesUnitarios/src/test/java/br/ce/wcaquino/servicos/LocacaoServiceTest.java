package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.Date;

import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
/**
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
 **/
import static org.junit.Assert.*;

public class LocacaoServiceTest {

    @Test
    public void testeLocacao(){
        // Cenário
        LocacaoService locacaoService = new LocacaoService();
        Usuario usuario = new Usuario("Usuário 1");
        Filme filme = new Filme("Filme 1", 2, 5.0);

        // Ação
        Locacao locacao = locacaoService.alugarFilme(usuario, filme);

        // Verificação
        assertEquals("Valor errado!", locacao.getValor(), 5.0, 0.01);
        /** Depreciado
        assertThat(locacao.getValor(), is(5.0));
        assertThat(locacao.getValor(), is(equalTo(5.0)));
        assertThat(locacao.getValor(), is(not(5.1)));
         **/
        assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
        assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)));

    }

}
