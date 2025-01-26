package com.broyojo.spotifywrappedclone.backend.account;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

public class UserDatabase {
    private final static String PATH = "users";
    private final Context context;
    private Set<User> users;

    public UserDatabase(Context context) {
        this.context = context;
        load();
    }

    private void load() {
        File file = new File(context.getFilesDir(), PATH);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                users = (Set<User>) ois.readObject();
            } catch (Exception e) {
                users = new HashSet<>();
                e.printStackTrace();
            }
        } else {
            users = new HashSet<>();
            save();
        }
    }

    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(PATH, Context.MODE_PRIVATE))) {
            oos.writeObject(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean containsUsername(String username) {
        for (User user : users) {
            if (user.getName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public User getUserByName(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }

        return null;
    }

    public void addUser(User user) {
        users.add(user);
        save();
    }

    public void removeUser(User user) {
        users.remove(user);
        save();
    }
}
