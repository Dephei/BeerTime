package giorgi.dundua.helpers;

import giorgi.dundua.beertime.Authorization;

import java.io.IOException;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

/**
 * This example shows how to fetch tokens if you are creating a foreground task/activity and handle
 * authentication exceptions.
 */
public class GetNameInForeground extends AbstractGetNameTask {

  public GetNameInForeground(Authorization activity, String email, String scope) {
      super(activity, email, scope);
  }


  /**
   * Get a authentication token if one is not available. If the error is not recoverable then
   * it displays the error message on parent activity right away.
   */
  @Override
  protected String fetchToken() throws IOException {
      try {
          return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
      } catch (UserRecoverableAuthException userRecoverableException) {
          // GooglePlayServices.apk is either old, disabled, or not present, which is
          // recoverable, so we need to show the user some UI through the activity.
          mActivity.handleException(userRecoverableException);
      } catch (GoogleAuthException fatalException) {
          onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
      }
      return null;
  }
}
