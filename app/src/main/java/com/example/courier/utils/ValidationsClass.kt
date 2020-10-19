package com.example.courier.utils

/*
 * Created by admin on 18-12-2017.
 */

class ValidationsClass {
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    /* public static ValidationsClass getInstance() {
        if (validations == null) {
            validations = new ValidationsClass();
        }
        return validations;
    }*/
    fun checkStringNull(string : String?) : Boolean {
        return string == null || string == "null" || string.isEmpty()
    }

    companion object {
        //  private static ValidationsClass validations = null;
        const val EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    }

    fun validateEmail(email : String) : Boolean {
        if (email.matches(emailPattern.toRegex())) {
            return true
        } else {
            return false
        }
    }
    /**
     * Check whether Google Play Services are available.
     *
     *
     * If not, then display dialog allowing user to update Google Play Services
     *
     * @return true if available, or false if not
     */
    /*  public boolean checkGooglePlayServicesAvailable(Activity mActivity) {
        final int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MyApplication.getInstance().getApplicationContext());
        if (status == ConnectionResult.SUCCESS)
            return true;
        Log.e("Google Play Services", "Google Play Services not available: " + GoogleApiAvailability.getInstance().getErrorString(status));
        if (GoogleApiAvailability.getInstance().isUserResolvableError(status)) {
            final Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(mActivity, status, 1);
            if (errorDialog != null)
                errorDialog.show();
        }
        return false;
    }*/
}
