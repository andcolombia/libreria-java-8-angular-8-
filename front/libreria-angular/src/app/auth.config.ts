// This api will come in the next version

import { AuthConfig } from 'angular-oauth2-oidc';

export const authConfig: AuthConfig = {
  issuer: 'https://qaautenticaciondigital.and.gov.co',
  redirectUri: window.location.origin + '/inicio',
  silentRefreshRedirectUri: window.location.origin + '/silent-refresh.html',
  clientId: 'spa-demo1QA',
  responseType: 'code',
  scope: 'openid profile email',
  showDebugInformation: true,
  sessionChecksEnabled: true,
};
