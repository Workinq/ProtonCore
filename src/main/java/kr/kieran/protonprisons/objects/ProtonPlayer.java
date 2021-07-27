package kr.kieran.protonprisons.objects;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class ProtonPlayer
{

    private final int id;
    public int getId() { return id; }

    private final UUID uniqueId;
    public UUID getUniqueId() { return uniqueId; }

    private int prestige;
    public int getPrestige() { return prestige; }
    public void setPrestige(int prestige) { this.prestige = prestige; }

    private int blocksMined;
    public int getBlocksMined() { return blocksMined; }
    public void setBlocksMined(int blocksMined) { this.blocksMined = blocksMined; }

    private final Timestamp createdAt;
    public Timestamp getCreationTime() { return createdAt; }

    private final Map<ProtonCurrency, Double> currencies;
    public Map<ProtonCurrency, Double> getCurrencies() { return Collections.unmodifiableMap(currencies); }
    public double getCurrencyBalance(ProtonCurrency currency) { return currencies.get(currency); }
    public void setCurrencyBalance(ProtonCurrency currency, double amount) { this.currencies.put(currency, amount); }

    public ProtonPlayer(int id, UUID uniqueId, int prestige, int blocksMined, Timestamp createdAt, Map<ProtonCurrency, Double> currencies)
    {
        this.id = id;
        this.uniqueId = uniqueId;
        this.prestige = prestige;
        this.blocksMined = blocksMined;
        this.createdAt = createdAt;
        this.currencies = currencies;
    }

}
