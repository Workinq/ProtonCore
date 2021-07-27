package kr.kieran.protonprisons.objects;

public class ProtonCurrency
{

    private final int id;
    public int getId() { return id; }

    private final String name;
    public String getName() { return name; }

    private final String symbol;
    public String getSymbol() { return symbol; }

    public ProtonCurrency(int id, String name, String symbol)
    {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
    }

}
