# Vibe Coding Global Guide V1.2.1

**Concept Author:** Mukhlis Amien (Inspired by Nicolas Zullo & ModernKataKupas Project Workflow)
**Version:** 1.2.1 (Minor clarifications revision)
**Initial Creation Date:** May 25, 2025
**Last Revised Date:** May 26, 2025
**Goal:** To build high-quality software efficiently through structured planning, clear human-AI collaboration, and rapid iteration.

## Core Vibe Coding Philosophy

1.  **Human as Lead Architect:** You (the human developer) are the strategic planner. AI is your highly competent execution assistant. Don't delegate fundamental planning to AI.
2.  **Context is King:** AI needs a deep understanding of the project. Provide all relevant information through a structured and always *up-to-date* "Memory Bank."
3.  **Iterate with "Baby Steps":** Break down large tasks into small, clear, and testable units (`baby-step.md`). This minimizes ambiguity and sets AI up for success.
4.  **Continuous Testing:** Every `baby-step` must be thoroughly tested before moving on. Quality is built incrementally.
5.  **Living Documentation:** Documents like `status-todo-suggestions.md`, `architecture.md`, and `progress.md` are living artifacts that are continuously updated, not created once and forgotten.

## Suggested Toolkit

1.  **Gemini (or similar AI for Planning & Analysis):** Used for understanding project context, generating initial planning documents, code reviews, and creating `status-todo-suggestions.md` and `baby-step.md`.
2.  **Jules (or your preferred AI Coding Assistant in your IDE):** Used for code implementation based on `baby-step.md`.
3.  **Cursor (or IDE with AI & Git Integration):** Used as the primary development environment, test execution, and version management with Git.
4.  **Git & GitHub (or other Version Control Platform):** For source code management and collaboration.

## Pre-Project Phase: Brainstorming & Initial Proposal

Before formally starting "Phase 0" of Vibe Coding, it's crucial to have a reasonably mature project idea.
* **Action (You):** Brainstorm until you have a complete overview in a minimal **project proposal**. This should include Chapter 1 (Introduction: Background, Problem Statement, Objectives), Chapter 2 (Literature Review/Theoretical Basis), and Chapter 3 (Methodology/Project Design). *Note: This proposal doesn't need to be perfect or overly formal; its purpose is to provide solid initial context for the AI in Phase 0.*

## Phase 0: Initial Project Setup (Preparation & Document Scaffolding)

**Goal:** To build a strong project foundation with clear planning documents and a ready repository.

* **Step 0.1: Project Initiation with AI & Repository**
    * **Action (You to Gemini):** Once your project proposal is ready, use the following prompt to start setting up the project documents. Ensure you *include the proposal content* in your interaction with Gemini.
    * **Prompt for Gemini:**
        ```
        I'm about to start vibe coding for a project described in the following proposal: [Paste your proposal content here or provide it as a separate file if the platform allows and reference it clearly]. Based on this vibe coding guide, please help me set up the initial document structure. This includes helping create initial drafts for:
        1. A Product/Game Design Document (PDD/GDD) in Markdown format (`product-design-doc.md`).
        2. Tech Stack Recommendations (`tech-stack.md`).
        3. An Initial Implementation Plan for the MVP (`implementation-plan.md`).
        Also, prepare a draft for the `README.md` file to be used in the GitHub repository, briefly explaining the project and how to run it (if foreseeable).
        ```
    * **Action (You):**
        * Create a new repository on GitHub (or your chosen platform).
        * Initialize it with a `README.md` (you can use Gemini's draft and refine it).

* **Step 0.2: Creating Core Planning Documents (With Gemini's Assistance)**
    * **Action (You & Gemini):**
        1.  **Product/Game Design Document (PDD/GDD):**
            * Use the draft from Step 0.1. Review and refine. Ensure the vision, key features, target users, and user flows (if any) are clear.
            * **Output:** `product-design-doc.md`
        2.  **Tech Stack:**
            * Use the draft from Step 0.1. Discuss with Gemini, consider pros and cons, and finalize technology choices.
            * **Output:** `tech-stack.md`
        3.  **Initial Implementation Plan (MVP):**
            * Use the draft from Step 0.1. Ensure this plan breaks down the MVP into smaller, manageable features or modules. Each item should have success criteria or a validation method.
            * **Output:** `implementation-plan.md`

* **Step 0.3: Prepare the "Memory Bank"**
    * **Action (You):**
        * Create your main project folder (if it doesn't exist).
        * Inside it, create a subfolder named `memory-bank`.
        * Add the following files to `memory-bank/`:
            * Your Project Proposal (e.g., `project-proposal.pdf` or `.md`)
            * `product-design-doc.md`
            * `tech-stack.md`
            * `implementation-plan.md`
            * `status-todo-suggestions.md` (Create this empty file. Gemini will populate it later).
            * `baby-step.md` (Create this empty file. Gemini will populate it later).
            * `architecture.md` (Create this empty file for architecture documentation as the project evolves).
            * `progress.md` (Create this empty file to track completed implementation steps).
        * Commit all these initial files to your Git repository with a clear commit message (e.g., "Initial project setup and planning documents").

* **Step 0.4: (Optional) Cursor Rules for AI**
    * If you're using Cursor and want its AI to follow specific rules:
        * In Cursor, open the chat and use the `/Generate Cursor Rules` command, selecting the `.md` files you've created (PDD, Tech Stack, Implementation Plan).
        * Review the generated rules. Ensure they emphasize modularity and best practices. You might need to adjust them manually.
        * Mark crucial rules (e.g., always read `architecture.md` or PDD before writing code) as "Always" rules.

---

## Phase 1: Environment Setup & Implementation Plan Clarification (Prompt Collection Phases 0 & 1)

**Goal:** Ensure the development environment is ready and the initial implementation plan is crystal clear and ambiguity-free for the AI before the first line of code is written.

* **Step 1.1: Environment Setup (You & Gemini)**
    * **Prompt for Gemini:**
        ```
        Read all files in the ./memory-bank directory to understand my project context. I am currently in the environment setup phase for coding. Please guide me in detail, based on the implementation-plan.md and tech-stack.md documents, for the necessary environment setup steps (e.g., specific library installations, initial configurations, recommended code folder structure).
        ```
    * **Action (You):** Follow Gemini's guidance to set up your development environment.

* **Step 1.2: Ensuring No Ambiguity in the Plan (You & Gemini)**
    * **Prompt for Gemini (Initial Clarification Iteration):**
        ```
        Reread all documents in ./memory-bank, especially implementation-plan.md. Is the plan clear enough to start implementing the first step of the MVP? Ask specific questions if any part needs 100% clarification for you before we create the first baby-step.
        ```
    * **Action (You):** Answer Gemini's questions in detail. If clarifications lead to changes, ask Gemini to help suggest updates to `implementation-plan.md` or other relevant documents in the `memory-bank`. Save those changes.
    * **Prompt for Gemini (Further Clarification Iteration - repeat if necessary):**
        ```
        Thanks for the clarifications. I've updated the [mention updated document name] in ./memory-bank. Please review it again. Is it 100% clear now, or is there anything else that needs to be clarified before we proceed?
        ```
    * **Action (You):** Repeat this Q&A and document update process until Gemini confirms everything is perfectly clear.
    * **Prompt for Gemini (Final Readiness Confirmation):**
        ```
        After all these clarifications, and assuming all documents in ./memory-bank are the most up-to-date, think carefully: is there anything crucial overlooked or still ambiguous that could hinder the implementation of the first baby-step? If not, we can proceed. Answer "YES, STILL ISSUES" or "NO, READY TO PROCEED". If "YES, STILL ISSUES", please explain what they are.
        ```
    * **Action (You):** If Gemini answers "YES, STILL ISSUES," resolve those ambiguities. Ensure everything is final before moving to the next phase. Commit all document changes to Git.

---

## Phase 2: Iterative Development Cycle (Baby Steps) (Prompt Collection Phase 3)

**Goal:** Implement features incrementally, ensuring quality at each step. This cycle is repeated for every *baby step*.

* **Step 2.1: Review Progress & Create Baby Step (You & Gemini)**
    * **Prompt for Gemini:**
        ```
        Read all files in ./memory-bank to understand my project context and the latest progress (see status-todo-suggestions.md and progress.md for history).
        Currently, [briefly explain the latest status, e.g., "all tests for the previous baby-step named '[last baby step name]' on commit [mention last commit hash if relevant] have passed" or "we are about to start implementing the feature/section '[feature name from implementation-plan.md]'"].
        If there's an existing codebase, please review it generally (or specific parts relevant to the next step, perhaps referring to certain files or directories).

        Your tasks now are:
        1.  Create/Update the status-todo-suggestions.md file. Its content should include:
            * Current project status (brief summary of recent progress, what was just completed).
            * A high-priority To-Do List for the near future (based on implementation-plan.md and current progress).
            * A very specific "Baby-Step To-Do List" suggestion for the next implementation step. This should be a small, logical, and testable unit of work.
        2.  After that, elaborate further on that "Baby-Step To-Do List" and create the content for the baby-step.md file. This file must contain extremely detailed, step-by-step instructions, as if for a junior developer. Eliminate all ambiguity. Each sub-task in baby-step.md must include:
            * A clear description of the task and its purpose.
            * Which files are likely to be created or modified.
            * Acceptance criteria or specific ways to test/validate the task's success (e.g., "pytest must pass," "output X should be visible in the UI," "API endpoint Y must return Z").
        ```
    * **Action (You):**
        * Review the `status-todo-suggestions.md` and `baby-step.md` generated by Gemini.
        * Ensure `baby-step.md` is truly detailed, clear, unambiguous, and each task is testable. Revise with Gemini if needed until you are 100% confident.
    * **Output:** `status-todo-suggestions.md` (updated), `baby-step.md` (new or updated). Save both files in `memory-bank`.

* **Step 2.2: Implement Baby Step (You & Jules/AI Coding Assistant)**
    * **Prompt for Jules (or other AI Coding Assistant in your IDE):**
        ```
        Read all documents in the ./memory-bank directory to get full context. Your current focus is the baby-step.md file. Implement all tasks described in baby-step.md sequentially and carefully. Ensure you follow the acceptance criteria and testing guidelines mentioned for each sub-task. If there are dependencies between sub-tasks, address them in order.
        ```
    * **Action (You):**
        * Guide Jules if necessary, especially if minor design decisions not covered in `baby-step.md` arise or if the AI struggles to interpret instructions.
        * After Jules (or your coding AI) completes the implementation:
            * Run all relevant tests (unit tests, integration tests, or manual tests based on validation criteria in `baby-step.md`).
            * Ensure `pytest` (or your testing framework) passes all tests, or all manual validation criteria are met. Debug any issues, either with the AI coder's help or on your own.
    * **Output:** Implemented and tested code.

* **Step 2.3: Update Progress & Documentation (You, can be AI-assisted for drafting)**
    * **Action (You):**
        * After the `baby-step.md` implementation is complete and all tests pass:
            1.  **Update `progress.md`:** Record the `baby-step` just completed (e.g., by referencing the `baby-step.md` filename or its brief description), completion date, and a short summary of work done and key outcomes.
            2.  **Update `architecture.md`:** If there were significant architectural changes, important new files, or major modifications to existing files, document them here. Explain their architectural role and how they affect the overall system.
            3.  **(Optional) Ask Jules/AI Coder for a summary:**
                * **Prompt for Jules:** *"The baby-step.md implementation is complete and all tests have passed. Provide a brief summary of the files created/modified and the main functionality successfully implemented. This will be used for a status update."*
                * You will use this summary as input when interacting with Gemini in the next Step 2.1.
    * **Output:** `progress.md` (updated), `architecture.md` (updated).

* **Step 2.4: Code Management & Synchronization (You & Cursor/Git)**
    * **Action (You in Cursor or terminal):**
        1.  `git pull` (to ensure you're working with the latest version, especially if collaborating).
        2.  Ensure all relevant documents in `memory-bank` (like `status-todo-suggestions.md` from Step 2.1, and `progress.md` and `architecture.md` from Step 2.3) are saved and ready to be staged for commit along with code changes.
        3.  `git add .` (or add specific changed files, including code and documents in `memory-bank`).
        4.  `git commit -m "Completed baby-step: [Name or brief description from baby-step.md]"`
        5.  `git push` to send changes to the remote repository.
        6.  After a successful push, delete the `baby-step.md` file from `memory-bank` (or archive it to a folder like `memory-bank/completed_baby_steps/`). This signals that the `baby-step` is done and you're ready for the next cycle.
    * **Output:** Code and related documentation committed and pushed to GitHub. `baby-step.md` is cleared/deleted for the next cycle.

* **Step 2.5: Repeat Cycle**
    * Return to Step 2.1 to create the next `baby-step.md`. Repeat this cycle until all features in `implementation-plan.md` (for MVP) are complete.

---

## Phase 3: Detailed Feature Addition & Refinement

**Goal:** After the basic functionality (MVP) is complete, add other features from the PDD/GDD, perform optimizations, and fix bugs.

* **For each new major feature (beyond the initial MVP):**
    1.  **Create a Specific Feature Implementation Plan:**
        * **Action (You & Gemini):** Similar to the initial `implementation-plan.md`, create a new file, e.g., `feature-X-implementation-plan.md`. In it, define small steps and tests for that feature. Add this file to the `memory-bank`.
    2.  **Follow the Iterative Development Cycle (Phase 2):** Use `feature-X-implementation-plan.md` as a guide to create `baby-step.md` files and implement iteratively as in Phase 2.

* **Bug Fixing & Issue Handling:**
    * If a prompt fails or the AI generates code that breaks the application: Use Cursor's "restore" feature or revert to the last Git commit (`git reset --hard HEAD~1` or `git revert <commit-hash>`). Refine your prompt or `baby-step.md`.
    * For JavaScript/Python/etc. errors: Open the browser console (for JS), view the error traceback, copy the error, and paste it into Cursor/AI for debugging help.
    * If completely stuck: Consider extracting the entire codebase (e.g., with a prompt asking the AI to analyze the folder structure and main files) and ask Gemini for help with full context to identify issues or provide architectural advice.

---

## Additional Tips

* **Better Prompts for the Planning AI (Gemini):**
    * "Take as much time as you need to do this correctly; I'm not in a rush. What's important is that you follow my instructions precisely and execute them perfectly."
    * "If there's any ambiguity in my instructions, always ask clarifying questions before proceeding."
    * "Act as a senior software architect helping me plan the implementation steps."
* **Better Prompts for the Coding AI (Jules):**
    * "Write clean, modular, readable, and efficient code."
    * "Add comments explaining complex logic where necessary."
    * "Ensure you follow the project's style guide if one exists (store it in `memory-bank/coding_style_guide.md`)."
* **AI Specialization (if using various models):**
    * Marketing/Copywriting: GPT-4.x, Claude 3 Opus
    * Graphic Design/2D Sprites: ChatGPT-4o, Midjourney, DALL-E 3
    * Music: Suno AI, AIVA
    * Sound Effects: ElevenLabs (for voice), other AI-based SFX platforms.
* **For Applications (Non-Game):** This workflow is largely the same. Replace GDD with PRD (Product Requirements Document). You can use prototyping tools like v0.dev, Figma (with AI plugins), or Kriya to create UI/UX prototypes first, and their output can be input for `product-design-doc.md`.

---

This guide is a living framework. Adapt it to your project's specific needs, the tools you use, and your personal preferences. The key is **thorough planning, clear communication with AI, and measurable iteration.** Happy "Vibe Coding"!