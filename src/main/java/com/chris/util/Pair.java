package com.chris.util;

import java.io.Serializable;

public  class Pair<First,Second> implements Serializable {
    protected First  first;
    protected Second  second;

    public  Pair(){}
    public Pair(First first,Second second){
        this.first = first;
        this.second = second;
    }


    @Override
    public String toString() {
        return this.first + "->" + this.second;
    }

    @Override
    public int hashCode() {
        return this.first.hashCode() * 13 + (this.second == null ? 0 : this.second.hashCode());
    }

    @Override
    public boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (!(var1 instanceof Pair)) {
            return false;
        } else {
            Pair var2 = (Pair)var1;
            if (this.first != null) {
                if (!this.first.equals(var2.first)) {
                    return false;
                }
            } else if (var2.first != null) {
                return false;
            }

            if (this.second != null) {
                if (!this.second.equals(var2.second)) {
                    return false;
                }
            } else if (var2.second != null) {
                return false;
            }

            return true;
        }
    }

    public final First first(){return first;}
    public final Second second(){return second;}

    public final void first(First first){ this.first = first;}
    public final void second(Second second){this.second = second;}

    public static <First,Second> Pair of( First  first,  Second  second)
    {
        return new Pair(first,second);
    }

}
