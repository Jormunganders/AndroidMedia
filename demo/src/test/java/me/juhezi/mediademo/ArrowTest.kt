package me.juhezi.mediademo

import arrow.core.*
import arrow.core.Either.Right
import arrow.core.Either.Left
import arrow.core.Validated.Companion.invalidNel
import arrow.core.computations.either
import arrow.core.computations.nullable
import arrow.typeclasses.Semigroup
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ArrowTest {

    @Test
    fun test(): Unit = runBlocking {
        val fields = listOf(
            FormField("Invalid Email Domain Label", "nowhere.com"),
            FormField("Too Long Email Label", "nowheretoolong${(0..251).map { "g" }}"), //this fails
            FormField("Valid Email Label", "getlost@nowhere.com")
        )
        Rules(Strategy.FailFast, fields)
    }

    //----------------------
    // Nullable types
    /**
     * Nullable types
     */
    private suspend fun hello(): String? =
        nullable {
            function().bind()
        }

    private fun function(): String? = null

    //----------------------
    // Either

    private fun gala(enable: Boolean): Either<Int, Boolean> = if (enable) {
        Right(true)
    } else {
        Left(30)
    }

    //----------------------
    // Strategies
    sealed class ValidationError(val msg: String) {

        data class DoesNotContain(val value: String) : ValidationError("Did not contain $value")
        data class MaxLength(val value: Int) : ValidationError("Exceeded length of $value")
        data class NotAnEmail(val reasons: Nel<ValidationError>) :
            ValidationError("Not a valid email")

    }

    data class FormField(val label: String, val value: String)
    data class Email(val value: String)

    sealed class Strategy {
        object FailFast : Strategy()
        object ErrorAccumulation : Strategy()
    }

    object Rules {

        private fun FormField.contains(needle: String): ValidatedNel<ValidationError, FormField> =
            if (value.contains(needle, false)) validNel()
            else ValidationError.DoesNotContain(needle).invalidNel()

        private fun FormField.maxLength(maxLength: Int): ValidatedNel<ValidationError, FormField> =
            if (value.length <= maxLength) validNel()
            else ValidationError.MaxLength(maxLength).invalidNel()

        private fun FormField.validateErrorAccumulate(): ValidatedNel<ValidationError, Email> =
            contains("@").zip(
                Semigroup.nonEmptyList(), // accumulates errors in a non empty list, can be omited for NonEmptyList
                maxLength(250)
            ) { _, _ -> Email(value) }
                .handleErrorWith { ValidationError.NotAnEmail(it).invalidNel() }

        /** either blocks support binding over Validated values with no additional cost or need to convert first to Either **/
        private fun FormField.validateFailFast(): Either<Nel<ValidationError>, Email> =
            either.eager {
                contains("@").bind() // fails fast on first error found
                maxLength(250).bind()
                Email(value)
            }

        operator fun invoke(
            strategy: Strategy,
            fields: List<FormField>
        ): Either<Nel<ValidationError>, List<Email>> =
            when (strategy) {
                Strategy.FailFast ->
                    fields.traverseEither { it.validateFailFast() }
                Strategy.ErrorAccumulation ->
                    fields.traverseValidated(Semigroup.nonEmptyList()) {
                        it.validateErrorAccumulate()
                    }.toEither()
            }

    }

}