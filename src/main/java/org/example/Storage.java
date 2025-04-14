package org.example;
import java.util.ArrayList;

public class Storage {
    private ArrayList<String> quoteList;
    Storage()
    {
        quoteList = new ArrayList<>();
        quoteList.add("Прими мои самые теплые поздравления с днем рождения! Желаю тебе крепкого здоровья, неугасаемого оптимизма и безграничного счастья. Пусть жизнь будет наполнена яркими красками, интересными событиями и приятными сюрпризами. Желаю, чтобы все твои мечты сбывались, а каждый день приносил новые возможности для самореализации!");
        quoteList.add("С днем рождения! Желаю, чтобы каждый день был наполнен радостью, улыбками и теплом близких людей. Пусть сбудутся самые заветные мечты, а жизнь дарит только приятные моменты!");
        quoteList.add("От всей души поздравляю с днем рождения! Желаю ярких впечатлений, новых горизонтов и незабываемых приключений. Пусть успех сопутствует во всех делах, а в сердце всегда живет любовь и гармония!");
    }

    String getRandQuote()
    {
        int randValue = (int)(Math.random() * quoteList.size());

        return quoteList.get(randValue);
    }
}
