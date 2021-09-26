package bugbusters.everyonecodes.java.usermanagement.rolemanagement.organization;

import bugbusters.everyonecodes.java.usermanagement.data.UserPrivateDTO;

import javax.validation.Valid;
import java.util.Objects;

// REVIEW: It seems like a simple wrapper to the UserPrivateDTO. What is the purpose if this DTO?
public class ClientPrivateDTO {

    @Valid
    private UserPrivateDTO userPrivateDTO;

    public ClientPrivateDTO() {
    }

    public ClientPrivateDTO(UserPrivateDTO userPrivateDTO) {
        this.userPrivateDTO = userPrivateDTO;
    }

    public UserPrivateDTO getUserPrivateDTO() {
        return userPrivateDTO;
    }

    public void setUserPrivateDTO(UserPrivateDTO userPrivateDTO) {
        this.userPrivateDTO = userPrivateDTO;
    }

    // REVIEW: I will just state it here once and that's my personal opinion - I hate these boilerplate implementations
    //         I know, I know, your IDE creates those automatically if you create a new class BUT it is a lot of code
    //         that you might never need and have to read every time.
    //         I would only implement those if you really need them. If you don't, just throw them out. Why? Because you
    //         will start to read over those things and if someday there is a bug that effects incorrect comparison or
    //         a strange lookup problem in hashmaps you don't know which objects have their own logic. If you implement
    //         it only if your logic needs it, you know where to look.
    //         And for "toString": I tend to use the debugger to get information about objects during development. In
    //         production these functions can really help, but sometimes they are also a real problem. Think about
    //         sensitive data (email, password, age, gender, and so on) that is dumped to the log every time something
    //         logs that object.
    //         TL;DR: You are the programmer not your IDE, just take a moment to make sure that you really want this
    //         code there or not and why.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientPrivateDTO that = (ClientPrivateDTO) o;
        return Objects.equals(userPrivateDTO, that.userPrivateDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPrivateDTO);
    }

    @Override
    public String toString() {
        return "ClientPrivateDTO{" +
                "userPrivateDTO=" + userPrivateDTO +
                '}';
    }
}
