package br.ce.wcaquino.matchers;

import static java.util.Calendar.MONDAY;

public class MatchersProprios {

    public static DiaSemanaMatchers caiEm(Integer diaSemana) { return new DiaSemanaMatchers(diaSemana); }

    public static DiaSemanaMatchers caiNumaSegunda() { return new DiaSemanaMatchers(MONDAY); }

    public static DataDiferencaDiasMatcher ehHoje() { return new DataDiferencaDiasMatcher(0); }

    public static DataDiferencaDiasMatcher ehHojeComDiferencaDias(Integer qtdDias) {
        return new DataDiferencaDiasMatcher(qtdDias);
    }

}
