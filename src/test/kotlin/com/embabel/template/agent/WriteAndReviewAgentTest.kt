package com.embabel.template.agent

import com.embabel.agent.domain.io.UserInput
import com.embabel.agent.testing.unit.FakeOperationContext
import com.embabel.agent.testing.unit.LlmInvocation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant

/**
 * Unit tests for the WriteAndReviewAgent class.
 * Tests the agent's ability to craft and review stories based on user input.
 */
internal class WriteAndReviewAgentTest {

    /**
     * Tests the story crafting functionality of the WriteAndReviewAgent.
     * Verifies that the LLM call contains expected content.
     */
    @Test
    fun testCraftStory() {
        // Create agent with word limits: 200 min, 400 max
        val agent = WriteAndReviewAgent(200, 400)

        // Create fake context and set expected response
        val context = FakeOperationContext.create()
        context.expectResponse(Story("Once upon a time, Sir Galahad..."))

        // Execute the story crafting
        val story = agent.craftStory(UserInput("Tell me a story about a brave knight", Instant.now()), context)

        // Verify the LLM invocation contains expected content
        val llmInvocation: LlmInvocation =
            context.llmInvocations.singleOrNull()
                ?: error("Expected a single LLM invocation, not ${context.llmInvocations.size}")

        // Verify the messages contain the expected keyword
        val messagesText = llmInvocation.messages.toString()
        Assertions.assertTrue(
            messagesText.contains("knight"),
            "Expected prompt to contain 'knight'"
        )
    }

    /**
     * Tests the story review functionality of the WriteAndReviewAgent.
     * Verifies that the review process includes expected prompt content.
     */
    @Test
    fun testReview() {
        // Create agent with word limits
        val agent = WriteAndReviewAgent(200, 400)

        // Set up test data
        val userInput = UserInput("Tell me a story about a brave knight", Instant.now())
        val story = Story("Once upon a time, Sir Galahad...")

        // Create fake context and set expected response
        val context = FakeOperationContext.create()
        context.expectResponse("A thrilling tale of bravery and adventure!")

        // Execute the review
        agent.reviewStory(userInput, story, context)

        // Verify the LLM invocation contains expected content
        val llmInvocation: LlmInvocation =
            context.llmInvocations.singleOrNull()
                ?: error("Expected a single LLM invocation, not ${context.llmInvocations.size}")

        val messagesText = llmInvocation.messages.toString()
        Assertions.assertTrue(
            messagesText.contains("knight"),
            "Expected prompt to contain 'knight'"
        )
        Assertions.assertTrue(
            messagesText.contains("review"),
            "Expected prompt to contain 'review'"
        )
    }
}