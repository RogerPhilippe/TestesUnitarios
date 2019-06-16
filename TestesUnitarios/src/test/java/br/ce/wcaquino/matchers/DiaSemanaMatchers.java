package br.ce.wcaquino.matchers;

import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DiaSemanaMatchers extends TypeSafeMatcher<Date> {

    private Integer mDiaSemana;

    public DiaSemanaMatchers(Integer diaSemana) {

        this.mDiaSemana = diaSemana;

    }

    @Override
    protected boolean matchesSafely(Date data) { return DataUtils.verificarDiaSemana(data, mDiaSemana); }

    @Override
    public void describeTo(Description description) {
        Calendar data = Calendar.getInstance();
        data.set(Calendar.DAY_OF_WEEK, mDiaSemana);
        String dataExtenso =
                data.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
        description.appendText(dataExtenso);
    }
}
