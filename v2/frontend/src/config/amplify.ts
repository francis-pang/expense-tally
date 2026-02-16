import { Amplify } from 'aws-amplify';

const userPoolId = import.meta.env.VITE_COGNITO_USER_POOL_ID;
const clientId = import.meta.env.VITE_COGNITO_CLIENT_ID;
const domain = import.meta.env.VITE_COGNITO_DOMAIN;
const redirectUrl = import.meta.env.VITE_REDIRECT_URL;

const cognitoConfig: Record<string, unknown> = {
  userPoolId: userPoolId || '',
  userPoolClientId: clientId || '',
};

if (domain && redirectUrl) {
  cognitoConfig.loginWith = {
    oauth: {
      domain: domain.replace(/^https?:\/\//, ''),
      scopes: ['openid', 'email', 'profile'],
      redirectSignIn: [redirectUrl],
      redirectSignOut: [redirectUrl],
      responseType: 'code',
    },
  };
}

Amplify.configure({
  Auth: {
    Cognito: cognitoConfig,
  },
});
