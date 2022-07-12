package net.breezeware.propel.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.breezeware.propel.annotation.Column;
import net.breezeware.propel.annotation.PrimaryKey;

@Data
@NoArgsConstructor
public class Transaction {

    @PrimaryKey
    private int id;
    @Column
    private String type;
    @Column
    private int amount;
    @Column
    private String src;
    @Column
    private String dst;

    public Transaction(String type, int amount, String src, String dst) {
        this.type = type;
        this.amount = amount;
        this.src = src;
        this.dst = dst;
    }
}
