package specifications;

import com.google.common.base.Joiner;
import com.theoryinpractise.specdown.concordion.Spec;
import com.theoryinpractise.specdown.concordion.SpecException;

import static org.fest.assertions.Assertions.assertThat;

public class Tutorial {

    @Spec("The greeting for user (.*) will be: (.*)")
    public void checkGreeting(String username, String expectedGreeting) {
        assertThat(getGreetingFor(username)).isEqualTo(expectedGreeting);
    }

    @Spec("The full name (.*) will be broken into first name (.*) and last name (.*).")
    public void checkFirstnameSplitting(String fullname, String firstname, String lastname) {

        String[] names = splitName(fullname);
        assertThat(names).hasSize(2);
        assertThat(names[0]).isEqualTo(firstname);
        assertThat(names[1]).isEqualTo(lastname);

    }

    @Spec("A full name with (\\d+) parts will be broken (\\d+) parts.")
    public String breakNameIntoParts(Integer partCount, Integer partCheck) {

        StringBuilder name = new StringBuilder();
        for (int i = 0; i < partCount; i++) {
            name.append("xx").append(" ");
        }

        String[] names = splitName(name.toString().trim());
        assertThat(names).hasSize(partCheck);
        assertThat(names[0]).isEqualTo("xx");
        assertThat(names[1]).isEqualTo("xx");

        return String.format("The name \"%s\" was split into: %s", name.toString().trim(), Joiner.on(", ").join(names));

    }



    // ****  implementation

    private String[] splitName(String fullname) {
        return fullname.split(" ");
    }


    private String getGreetingFor(String username) {
        return "Hello " + username + "!";
    }

}
