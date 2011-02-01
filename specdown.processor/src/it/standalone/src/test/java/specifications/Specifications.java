package specifications;

import com.theoryinpractise.specdown.concordion.Spec;
import com.theoryinpractise.specdown.concordion.SpecException;

import static org.fest.assertions.Assertions.assertThat;

public class Specifications {

    @Spec("write your specification documents")
    public void writerSpecs() {
        System.out.println("Writing specs");
    }

    @Spec("When the system is in brown out, service agreements should not be creatable.")
    public void testSystem() {
        System.out.println("testing the system");
    }


    @Spec("Create new (.*) customer.")
    public String createCustomer(String customer) {
        System.out.println("Creating a new customer named: " + customer);
        return "Hello " + customer;
    }

    @Spec("Changing (.*) output.")
    public void changingOutput(String s) {


        SpecException exception = new SpecException();
        exception.modifyGroup(1).withExpectedText("What the ?????");

        throw exception;

    }

    @Spec("Including assertion messages.")
    public void assertions() {
        assertThat(true).isFalse();
    }


}
