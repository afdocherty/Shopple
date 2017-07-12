package com.example.kathu228.shoplog.Models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

/**
 * Created by fmonsalve on 7/12/17.
 */

@ParseClassName("User")
public class User extends ParseUser {
    // WARNING: any fields/methods contained within a
    // ParseUser are protected (read-only) by other
    // users due to Parse ACL standards. Consider
    // access when adding additional fields/methods here!

    // NOTE: LoginActivity returns a ParseUser, meaning
    // casting is required to leverage unique User
    // fields/methods
}
