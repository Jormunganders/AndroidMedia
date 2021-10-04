package me.juhezi.mediademo.demo;

import com.google.auto.value.AutoValue;

@AutoValue
abstract class Animal {

    static Builder builder() {
        return new AutoValue_Animal.Builder();
    }

    abstract String name();

    abstract int numberOfLegs();

    @AutoValue.Builder
    abstract static class Builder {
        abstract Builder setName(String value);

        abstract Builder setNumberOfLegs(int value);

        abstract Animal build();
    }

}