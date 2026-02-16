interface TellerConnectEnrollment {
  accessToken: string;
  user: { id: string };
  enrollment: { id: string; institution: { name: string } };
  signatures: string[];
}

interface TellerConnectOptions {
  applicationId: string;
  environment?: 'sandbox' | 'development' | 'production';
  products?: Array<'transactions' | 'balance' | 'identity' | 'verify' | 'verify.instant'>;
  institution?: string;
  enrollmentId?: string;
  selectAccount?: 'disabled' | 'single' | 'multiple';
  nonce?: string;
  connectToken?: string;
  onInit?: () => void;
  onSuccess: (enrollment: TellerConnectEnrollment) => void;
  onExit?: () => void;
  onFailure?: (failure: { type: string; code: string; message: string }) => void;
}

interface TellerConnectInstance {
  open: () => void;
  destroy: () => void;
}

interface TellerConnectStatic {
  setup: (options: TellerConnectOptions) => TellerConnectInstance;
}

interface Window {
  TellerConnect: TellerConnectStatic;
}
