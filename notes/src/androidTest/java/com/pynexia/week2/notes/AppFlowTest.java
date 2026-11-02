package com.pynexia.week2.notes;
import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class AppFlowTest {

    private void idle() throws Exception {
        long deadline = System.currentTimeMillis() + 10000;
        do {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            if (EntryStore.get(InstrumentationRegistry.getInstrumentation().getTargetContext()).isIdle()) return;
            Thread.sleep(25);
        } while (System.currentTimeMillis() < deadline);
        fail("Storage did not become idle.");
    }
    private void snapshot(String name) throws Exception {
        idle(); Thread.sleep(400);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        android.graphics.Bitmap bitmap = InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        java.io.File folder = new java.io.File(InstrumentationRegistry.getInstrumentation().getTargetContext().getExternalFilesDir(null), "evidence");
        if (!folder.exists() && !folder.mkdirs()) throw new java.io.IOException("Cannot create screenshot directory");
        try (java.io.FileOutputStream stream = new java.io.FileOutputStream(new java.io.File(folder, name + ".png"))) { assertTrue(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)); }
        finally { bitmap.recycle(); }
    }
    @org.junit.Before public void clearEntries() throws Exception {
        EntryStore store = EntryStore.get(InstrumentationRegistry.getInstrumentation().getTargetContext());
        store.submit(() -> { for (Entry entry : store.database.all(false)) store.database.delete(entry.id); return true; }, (value, error) -> {});
        idle();
    }

    @Test public void createsEditsSearchesAndDeletes() throws Exception {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            idle(); onView(withId(R.id.add)).perform(click());
            onView(withId(R.id.save)).perform(scrollTo(), click());
            onView(withId(R.id.title_input)).check(matches(hasErrorText("Enter 1-120 characters.")));
            onView(withId(R.id.title_input)).perform(scrollTo(), replaceText("A place for new ideas"), closeSoftKeyboard());
            onView(withId(R.id.body_input)).perform(scrollTo(), replaceText("Capture one idea, shape it, and take the next small step."), closeSoftKeyboard());
            
            onView(withId(R.id.title_input)).check((view, error) -> assertNull(((android.widget.EditText)view).getError()));
            snapshot("notes-editor");
            onView(withId(R.id.save)).perform(scrollTo(), click()); idle();
            onView(withText("A place for new ideas")).check(matches(isDisplayed()));
            snapshot("notes-list");
            scenario.recreate(); idle();
            onView(withText("A place for new ideas")).check(matches(isDisplayed()));
            onView(withContentDescription("Edit A place for new ideas")).perform(click());
            onView(withId(R.id.title_input)).perform(scrollTo(), replaceText("Updated entry"), closeSoftKeyboard());
            onView(withId(R.id.save)).perform(scrollTo(), click()); idle();
            onView(withText("Updated entry")).check(matches(isDisplayed()));
            onView(withId(R.id.search)).perform(replaceText("absent"), closeSoftKeyboard());
            onView(withText("No matches here.")).check(matches(isDisplayed()));
            onView(withId(R.id.search)).perform(replaceText("Updated"), closeSoftKeyboard());
            onView(withText("Updated entry")).check(matches(isDisplayed()));
            onView(withContentDescription("Delete Updated entry")).perform(click());
            onView(withText("Cancel")).perform(click());
            onView(withText("Updated entry")).check(matches(isDisplayed()));
            onView(withContentDescription("Delete Updated entry")).perform(click());
            onView(withId(android.R.id.button1)).perform(click()); idle();
            onView(withId(R.id.empty)).check(matches(isDisplayed()));
        }
    }
    @Test public void preservesEditorDraftOnRecreation() throws Exception {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), EditorActivity.class);
        try (ActivityScenario<EditorActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.title_input)).perform(replaceText("Draft title"), closeSoftKeyboard());
            onView(withId(R.id.body_input)).perform(replaceText("Capture one idea, shape it, and take the next small step."), closeSoftKeyboard());
            scenario.recreate();
            onView(withId(R.id.title_input)).check(matches(withText("Draft title")));
            onView(withId(R.id.body_input)).check(matches(withText("Capture one idea, shape it, and take the next small step.")));
        }
    }
}