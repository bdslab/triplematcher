package it.unicam.cs.bdslab.triplematcher.models.filters;

public class FilterBuilder {
    private final static MatchFilter baseFilter = (rna, pair) -> {
        return true;
    };
    private MatchFilter filter;

    public FilterBuilder() {
        this.filter = baseFilter;
    }

    public FilterBuilder addFilter(MatchFilter newFilter) {
        if (this.filter == baseFilter)
            this.filter = newFilter;
        else
            this.filter = this.filter.and(newFilter);
        return this;
    }


    public FilterBuilder clear() {
        return new FilterBuilder();
    }

    public MatchFilter build() {
        return (rna, pair) -> this.filter.test(rna, pair);
    }

}
