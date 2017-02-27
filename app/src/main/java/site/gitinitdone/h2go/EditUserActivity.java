package site.gitinitdone.h2go;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class EditUserActivity extends AppCompatActivity {

    private GetUserInfoAPI getUserInfo = null;
    private UserAccount userAccount = null;
    private EditUserInfoAPI editUserInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getUserInfo = new GetUserInfoAPI();
        getUserInfo.execute((Void) null);
    }

    public void cancelEdits(View view) {
        finish();
        Toast.makeText(getApplicationContext(), "Discarded changes.", Toast.LENGTH_LONG).show();
    }

    public void submitEdits(View view) {

        Map<String, String> arguments = new HashMap<>();

        System.out.println("Edit User 1");

        EditText firstName = (EditText) findViewById(R.id.firstNameFieldEdit);
        String firstNameText = firstName.getText().toString().trim();
        if (firstNameText.length() == 0) {
            showErrorOnField(firstName, "First name is too short.");
            return;
        }

        System.out.println("Edit User 2");


        EditText lastName = (EditText) findViewById(R.id.lastNameFieldEdit);
        String lastNameText = lastName.getText().toString().trim();
        if (lastNameText.length() == 0) {
            showErrorOnField(lastName, "Last name is too short.");
            return;
        }

        System.out.println("Edit User 3");

        EditText email = (EditText) findViewById(R.id.emailFieldEdit);
        String emailText = email.getText().toString().trim();
        if (!validateEmail(emailText)) {
            showErrorOnField(email, "Email is not valid.");
            return;
        }

        EditText address = (EditText) findViewById(R.id.addressLineFieldEdit);
        EditText city = (EditText) findViewById(R.id.cityFieldEdit);
        EditText stateZip = (EditText) findViewById(R.id.stateZipFieldEdit);

        String addressText = address.getText().toString().trim();
        String cityText = city.getText().toString().trim();
        String stateZipText = stateZip.getText().toString().trim();

        System.out.println("Edit User 4");

        String addressFullText = addressText + "~" + cityText + "~" + stateZipText;
        if (addressText.length() == 0) {
            showErrorOnField(address,"Address is too short.");
            return;
        }
        if (cityText.length() == 0) {
            showErrorOnField(city, "City is too short.");
            return;
        }
        if (stateZipText.length() == 0) {
            showErrorOnField(stateZip, "State / Zip Code is too short.");
            return;
        }


        System.out.println("Edit User 5");

        Spinner title = (Spinner) findViewById(R.id.titleSpinnerEdit);
        String titleText = title.getSelectedItem().toString().trim();

        System.out.println("Edit User 6");

        arguments.put("firstName", firstNameText);
        arguments.put("lastName", lastNameText);
        arguments.put("email", emailText);
        arguments.put("address", addressFullText);
        arguments.put("title", titleText);

        System.out.println("Edit User 7");

        editUserInfo = new EditUserInfoAPI(arguments);
        editUserInfo.execute((Void) null);

    }

    private boolean validateEmail(String emailText) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(emailText);
        return m.matches();
    }

    private void showErrorOnField(EditText field, String message) {
        field.setError(message);
        field.requestFocus();
    }

    //************************************
    //************************************

    class GetUserInfoAPI extends AsyncTask<Void, Void, Boolean> {

        private CookieManager cookieManager;

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            cookieManager = LoginActivity.msCookieManager;

            System.out.println("--------1--------");
            URL url = null;
            try {
                url = new URL("http://www.gitinitdone.site/api/users");
            } catch (MalformedURLException e) {
                System.out.println("--- Error Here 1 ---");
                e.printStackTrace();
            }
            URLConnection con = null;
            try {
                con = url.openConnection();
            } catch (IOException e) {
                System.out.println("--- Error Here 2 ---");
                e.printStackTrace();
            }
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestProperty("Cookie",
                    TextUtils.join(";",  cookieManager.getCookieStore().getCookies()));

            try {
                http.setRequestMethod("GET"); // PUT is another valid option
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            System.out.println("--- Reached Here 3 ---");

            //http.setDoOutput(true);

            System.out.println("End of Part 1");

            System.out.println("End of Part 2");

            try {
                http.connect();
            } catch (IOException e) {
                System.out.println("--- Error Here 5 ---");
                e.printStackTrace();
            }

            BufferedInputStream bis = null;
            System.out.println(http.getRequestMethod());
            try {
                if (http.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                    bis = new BufferedInputStream(http.getInputStream());
                } else {
                    bis = new BufferedInputStream(http.getErrorStream());
                }
            } catch (IOException e) {
                System.out.println("--- Error Here 7 ---");
                e.printStackTrace();
            }

            byte[] contents = new byte[1024];

            int bytesRead = 0;
            String response = "";
            try {
                while((bytesRead = bis.read(contents)) != -1) {
                    response += new String(contents, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("--------2--------");
            System.out.println(response);
            System.out.println("--------3--------");

            boolean validData = !response.contains("Must be logged in");
            if (validData) {
                JSONObject json = null;
                UserAccount currentUser = null;
                try {
                    json = new JSONObject(response);
                    String username = json.getString("username");
                    String firstName = json.getString("firstName");
                    String lastName = json.getString("lastName");
                    String address = json.getString("address");
                    String email = json.getString("email");

                    String titleString = json.getString("title").toUpperCase();
                    UserAccount.Title title = UserAccount.Title.valueOf(titleString.substring(0, titleString.length() - 1));

                    UserAccount.AccountType type = UserAccount.AccountType.valueOf(json.getString("userType").toUpperCase());

                    currentUser = new UserAccount(username, title, firstName, lastName, address, email, type);
                    saveAccountInfo(currentUser);
                    return true;

                } catch (JSONException e) {
                    System.out.println("Failed converting response to JSON!!!");
                    e.printStackTrace();
                }

            } else {
                System.out.println("Log in cookie did not work. GET request did not work.");
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //mAuthTask = null;
            //showProgress(false);

            if (success) {
                populateFields(userAccount);
            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);
        }

        private void saveAccountInfo(UserAccount account) {
            userAccount = account;
        }
    }

    private void populateFields(UserAccount accountInfo) {


        EditText firstName = (EditText) findViewById(R.id.firstNameFieldEdit);
        firstName.setText(accountInfo.getFirstName());

        EditText lastName = (EditText) findViewById(R.id.lastNameFieldEdit);
        lastName.setText(accountInfo.getLastName());

        EditText email = (EditText) findViewById(R.id.emailFieldEdit);
        email.setText(accountInfo.getEmail());

        String[] addressParts = accountInfo.getAddress().split("~");
        EditText address = (EditText) findViewById(R.id.addressLineFieldEdit);
        address.setText(addressParts[0]);

        EditText city = (EditText) findViewById(R.id.cityFieldEdit);
        city.setText(addressParts[1]);

        EditText stateZip = (EditText) findViewById(R.id.stateZipFieldEdit);
        stateZip.setText(addressParts[2]);

        Spinner title = (Spinner) findViewById(R.id.titleSpinnerEdit);
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, UserAccount.Title.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        title.setAdapter(adapter);
        title.setSelection(accountInfo.getTitle().ordinal());

        TextView username = (TextView) findViewById(R.id.formUsernameFieldEdit);
        username.setText("Username: " + accountInfo.getUsername());

        TextView userType = (TextView) findViewById(R.id.formUserTypeFieldEdit);
        userType.setText("User Account Type: " + accountInfo.getUserType().toString());
    }

    //***************************************************
    //***************************************************
    //***************************************************
    //***************************************************


    class EditUserInfoAPI extends AsyncTask<Void, Void, Boolean> {

        private CookieManager cookieManager;

        private Map<String, String> data;

        EditUserInfoAPI(Map<String, String> data) {
            this.data = data;
            cookieManager = LoginActivity.msCookieManager;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            System.out.println("--------1--------");
            URL url = null;
            try {
                url = new URL("http://www.gitinitdone.site/api/users/edit");
            } catch (MalformedURLException e) {
                System.out.println("--- Error Here 1 ---");
                e.printStackTrace();
            }
            URLConnection con = null;
            try {
                con = url.openConnection();
            } catch (IOException e) {
                System.out.println("--- Error Here 2 ---");
                e.printStackTrace();
            }
            HttpURLConnection http = (HttpURLConnection) con;
            try {
                http.setRequestMethod("POST"); // PUT is another valid option
                System.out.println("--- Reached Here 3 ---");

            } catch (ProtocolException e) {
                e.printStackTrace();
            }

            if (cookieManager.getCookieStore().getCookies().size() > 0) {
                // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
                http.setRequestProperty("Cookie",
                        TextUtils.join(";",  cookieManager.getCookieStore().getCookies()));
            }
            http.setDoOutput(true);

            System.out.println("End of Part 1");


            String result = "";
            for (Map.Entry<String, String> entry : data.entrySet())
                try {
                    result += "&" + (URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                            + URLEncoder.encode(entry.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    System.out.println("--- Error Here 4 ---");
                    e.printStackTrace();
                }
            result = result.substring(1);
            byte[] out = result.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            System.out.println("End of Part 2");

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            try {
                http.connect();
            } catch (IOException e) {
                System.out.println("--- Error Here 5 ---");
                e.printStackTrace();
            }
            try {
                try (OutputStream os = http.getOutputStream()) {
                    os.write(out);
                }
            } catch (IOException e) {
                System.out.println("--- Error Here 6 ---");
                e.printStackTrace();
            }

            System.out.println("End of Part 3");

            // Do something with http.getInputStream()

            BufferedInputStream bis = null;

            try {
                if (http.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                    bis = new BufferedInputStream(http.getInputStream());
                } else {
                    bis = new BufferedInputStream(http.getErrorStream());
                }
            } catch (IOException e) {
                System.out.println("--- Error Here 7 ---");
                e.printStackTrace();
            }

            byte[] contents = new byte[1024];

            int bytesRead = 0;
            String response = "";
            try {
                while ((bytesRead = bis.read(contents)) != -1) {
                    response += new String(contents, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("--------2--------");
            System.out.println(response);
            System.out.println("--------3--------");

            System.out.println(!response.toLowerCase().contains("unauthorized"));

            if (response.toLowerCase().contains("unauthorized")) {
                System.out.println("Error at the end of the editing Do in Background.");
            }

            return (!response.toLowerCase().contains("unauthorized"));
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            editUserInfo = null;
            //showProgress(false);

            if (success) {
                System.out.println("Edited Data TRUE");
                Toast.makeText(getBaseContext(), "User profile has been saved.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                System.out.println("Edited Data FALSE");
                Toast.makeText(getBaseContext(), "There was an error during editing.", Toast.LENGTH_LONG).show();

            }
        }

        @Override
        protected void onCancelled() {
            editUserInfo = null;
            //showProgress(false);
        }


    }

}