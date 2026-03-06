package org.pollub.catalog.model;

/**
 * Search criteria with manual Builder pattern for catalog queries.
 * Encapsulates all search parameters for book queries.
 */
public class SearchCriteria {
    private final String query;
    private final ItemStatus status;
    private final String publisher;
    private final String genres;
    private final int page;
    private final int size;
    private final String sort;

    private SearchCriteria(Builder builder) {
        this.query = builder.query;
        this.status = builder.status;
        this.publisher = builder.publisher;
        this.genres = builder.genres;
        this.page = builder.page;
        this.size = builder.size;
        this.sort = builder.sort;
    }

    public String getQuery() { return query; }
    public ItemStatus getStatus() { return status; }
    public String getPublisher() { return publisher; }
    public String getGenres() { return genres; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public String getSort() { return sort; }
    //Lab1 - Builder 3 Start

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Manual Builder for SearchCriteria with fluent API.
     */
    public static class Builder {
        private String query;
        private ItemStatus status;
        private String publisher;
        private String genres;
        private int page = 0;
        private int size = 20;
        private String sort;

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder status(ItemStatus status) {
            this.status = status;
            return this;
        }

        public Builder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder genres(String genres) {
            this.genres = genres;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder sort(String sort) {
            this.sort = sort;
            return this;
        }

        public SearchCriteria build() {
            return new SearchCriteria(this);
        }
    }
    //Lab1 End Builder 3

}
