package contactmanager;

import java.util.Scanner;
//import java.util.ArrayList;

public class ContactManagementSystem {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        UserAuth auth = new UserAuth();

        System.out.println("===== CONTACT MANAGEMENT SYSTEM =====");
        while(true) {
            boolean loggedIn = false;
            int userId = -1;
            String username = "";
//            ArrayList<Contact> username2 = new ArrayList<>();
            boolean isAdmin = false;

            while (!loggedIn) {
                System.out.println();
                System.out.println("1. Sign In");
                System.out.println("2. Sign Up");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int topChoice = -1;
                try {
                    topChoice = Integer.parseInt(input.nextLine().trim());
                } catch (Exception e) {
                    topChoice = -1;
                }

                if (topChoice == 1) {
                    System.out.print("Username: ");
                    username = input.nextLine().trim();
                    System.out.print("Password: ");
                    String password = input.nextLine();
                    userId = auth.signIn(username, password);
                    if (userId != -1) {
                        System.out.println("Login successful.");
                        loggedIn = true;
                        if (username.equalsIgnoreCase("admin"))
                            isAdmin = true;
                    } else {
                        System.out.println("Invalid username/password. Try again.");
                    }
                } else if (topChoice == 2) {
                    System.out.print("Choose username: ");
                    String newUser = input.nextLine().trim();
                    System.out.print("Choose password: ");
                    String newPass = input.nextLine();
                    String msg = auth.signUp(newUser, newPass);
                    System.out.println(msg);
                } else if (topChoice == 3) {
                    System.out.println("Exiting...");
                    input.close();
                    DBConnection.getInstance().closeConnection();
                    return;
                } else {
                    System.out.println("Invalid choice.");
                }
            }

//            AddContact add = new AddContact(userId);
//            EditContact edit = new EditContact(userId);
//            UpdateContact update = new UpdateContact(userId);
//            DeleteContact delete = new DeleteContact(userId);
//            ViewContact view = new ViewContact(userId);
            Admin admin = new Admin();
//            GroupManager groupManager = new GroupManager(userId);
//            EmergencyContactManager emergencyManager = new EmergencyContactManager(userId);
            Operations operations = new Operations(userId);

            boolean running = true;
            while (running) {
                System.out.println();
                System.out.println("===== USER MENU (" + username + ") =====");
                System.out.println("1. Add Contact");
                System.out.println("2. Edit Contact");
                System.out.println("3. Update Contact");
                System.out.println("4. View Contacts");
                System.out.println("5. Search Contacts");
                System.out.println("6. Admin Menu (admin only)");
                System.out.println("7. Group Contacts");
                System.out.println("8. Emergency Contacts");
                System.out.println("9. Delete Contact");
                System.out.println("10. Logout");

                System.out.print("Enter your choice: ");
                int choice = -1;
                try {
                    choice = Integer.parseInt(input.nextLine().trim());
                } catch (Exception e) {
                    choice = -1;
                }

                String result = "";
                if (choice == 1) {
                    System.out.print("Enter First Name: ");
                    String first = input.nextLine();
                    System.out.print("Enter Last Name: ");
                    String last = input.nextLine();
                    System.out.print("Enter Mobile Number: ");
                    String mobile = input.nextLine();
                    System.out.print("Enter Email (optional): ");
                    String email = input.nextLine();
                    result = operations.addContact(first, last, mobile, email);
                } else if (choice == 2) {
                    System.out.println(operations.viewContacts());
                    System.out.print("Enter position number to edit: ");
                    int posEdit = readPosition(input);
                    if (posEdit == -1) {
                        result = "Invalid position.";
                    } else {
                        System.out.print("New Mobile (leave blank to skip): ");
                        String newMob = input.nextLine();
                        System.out.print("New Email (leave blank to skip): ");
                        String newEmail = input.nextLine();
                        result = operations.editContactByIndex(posEdit, newMob, newEmail);
                    }
                } else if (choice == 3) {
                    System.out.println(operations.viewContacts());
                    System.out.print("Enter position number to update: ");
                    int posUpd = readPosition(input);
                    if (posUpd == -1) {
                        result = "Invalid position.";
                    } else {
                        System.out.print("Enter DOB (dd-mm-yyyy) (leave blank to skip): ");
                        String dob = input.nextLine();
                        System.out.print("Enter Second Number (leave blank to skip): ");
                        String second = input.nextLine();
                        System.out.print("Enter Instagram ID (leave blank to skip): ");
                        String insta = input.nextLine();
                        System.out.print("Enter Memory (leave blank to skip): ");
                        String memory = input.nextLine();
                        result = operations.updateContactByIndex(posUpd, dob, second, insta, memory);
                    }
                } else if (choice == 4) {
                    result = operations.viewContacts();
                } else if (choice == 5) {
                	System.out.print("Enter letters to search in first names: ");
                    String searchElement = input.nextLine();

                    var found =
                    		Operations.searchByFirstName(userId, searchElement);

                    if (found.isEmpty()) {
                        result = "No contacts found matching: " + searchElement;
                    } else {
                        StringBuilder out =
                                new StringBuilder("\n--- Search Results ---\n");
                        for (int i = 0; i < found.size(); i++) {
                            out.append(i + 1)
                               .append(". ")
                               .append(found.get(i))
                               .append("\n");
                        }
                        result = out.toString();
                    }
                } else if (choice == 6) {
                    if (!isAdmin) {
                        result = "Access denied. Admin only.";
                    } else {
                        adminMenu(input, admin);
                        result = "Back to user menu.";
                    }
                } else if (choice == 7) {
                    groupManagerMenu(input, operations);
                } else if (choice == 8) {
                    emergencyMenu(input, operations);
                } else if (choice == 9) {
                    System.out.println(operations.viewContacts());
                    System.out.print("Enter position number to delete: ");
                    int posDel = readPosition(input);
                    if (posDel == -1) {
                        result = "Invalid position.";
                    } else {
                        System.out.print("Are you sure you want to delete this contact? (y/n): ");
                        String confirm = input.nextLine().trim().toLowerCase();
                        if (confirm.equals("y") || confirm.equals("yes")) {
                            result = operations.deleteContactByIndex(posDel);
                        } else {
                            result = "Deletion cancelled.";
                        }
                    }
                } else if (choice == 10) {
                    running = false;
                    loggedIn = false;
                    isAdmin = false;
                    result = "Goodbye!";
                } else {
                    result = "Invalid choice.";
                }

                System.out.println(result);
            }
        }
    }

    private static int readPosition(Scanner input) {
        try {
            int pos = Integer.parseInt(input.nextLine().trim());
            if (pos < 1) return -1;
            return pos;
        } catch (Exception e) {
            return -1;
        }
    }

    private static void adminMenu(Scanner input, Admin admin) {
        boolean adminRun = true;
        while (adminRun) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. View All Deleted (grouped by user)");
            System.out.println("2. Recover a Deleted Contact");
//            System.out.println("3. View All Active Contacts (all users)");
            System.out.println("3. Exit Admin Menu");
            System.out.print("Enter your choice: ");
            int aChoice = -1;
            try {
                aChoice = Integer.parseInt(input.nextLine().trim());
            } catch (Exception e) {
                aChoice = -1;
            }

            String aResult = "";
            if (aChoice == 1) {
                aResult = admin.viewAllDeletedGrouped();
            } else if (aChoice == 2) {
                String deletedList = admin.viewAllDeletedGrouped();
                System.out.println(deletedList);
                System.out.print("Enter position number of contact to recover: ");
                int pos = readPosition(input);
                aResult = admin.recoverContactByPosition(pos);
//            } else if (aChoice == 3) {
////                aResult = admin.viewAllActiveContacts();
            } else if (aChoice == 3) {
                adminRun = false;
                aResult = "Exiting admin menu.";
            } else {
                aResult = "Invalid choice.";
            }
            System.out.println(aResult);
        }
    }

    private static void groupManagerMenu(Scanner input, Operations operations) {
        boolean groupMenuRun = true;
        while (groupMenuRun) {
            System.out.println("\n===== GROUP MENU =====");
            System.out.println("1. Assign Group to a Contact");
            System.out.println("2. View All Contacts by Group");
            System.out.println("3. Back to User Menu");
            System.out.print("Enter your choice: ");
            int gChoice = -1;
            try {
                gChoice = Integer.parseInt(input.nextLine().trim());
            } catch (Exception e) {
                gChoice = -1;
            }

            if (gChoice == 1) {
            	operations.assignGroup(input);
            } else if (gChoice == 2) {
            	operations.viewGroupedContacts();
            } else if (gChoice == 3) {
                groupMenuRun = false;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private static void emergencyMenu(Scanner input, Operations operations) {
        boolean emergencyMenuRun = true;
        while (emergencyMenuRun) {
            System.out.println("\n===== EMERGENCY MENU =====");
            System.out.println("1. Mark a contact as emergency");
            System.out.println("2. View emergency contacts");
            System.out.println("3. Back to User Menu");
            System.out.print("Enter your choice: ");
            int eChoice = -1;
            try {
                eChoice = Integer.parseInt(input.nextLine().trim());
            } catch (Exception e) {
                eChoice = -1;
            }

            if (eChoice == 1) {
            	operations.markEmergency(input);
            } else if (eChoice == 2) {
            	operations.viewEmergencyContacts();
            } else if (eChoice == 3) {
                emergencyMenuRun = false;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}
