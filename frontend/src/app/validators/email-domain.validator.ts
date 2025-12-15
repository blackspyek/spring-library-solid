import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

// Common email domain typos and their corrections
const DOMAIN_CORRECTIONS: Record<string, string> = {
  // Gmail variations
  'gmial.com': 'gmail.com',
  'gmai.com': 'gmail.com',
  'gmali.com': 'gmail.com',
  'gmall.com': 'gmail.com',
  'gmal.com': 'gmail.com',
  'gamil.com': 'gmail.com',
  'gnail.com': 'gmail.com',
  'gmail.co': 'gmail.com',
  'gmail.om': 'gmail.com',
  'gmail.cm': 'gmail.com',
  'gmail.con': 'gmail.com',
  'gmail.cpm': 'gmail.com',
  'gmail.ocm': 'gmail.com',
  'gmaill.com': 'gmail.com',
  'gmaiil.com': 'gmail.com',
  'gimail.com': 'gmail.com',
  'gemail.com': 'gmail.com',

  // Hotmail/Outlook variations
  'hotmal.com': 'hotmail.com',
  'hotmai.com': 'hotmail.com',
  'hotmial.com': 'hotmail.com',
  'hotmali.com': 'hotmail.com',
  'hotamil.com': 'hotmail.com',
  'hotmail.co': 'hotmail.com',
  'hotmail.con': 'hotmail.com',
  'hotmaill.com': 'hotmail.com',
  'hitmail.com': 'hotmail.com',
  'hotnail.com': 'hotmail.com',
  'outlok.com': 'outlook.com',
  'outloo.com': 'outlook.com',
  'outlook.co': 'outlook.com',
  'outlook.con': 'outlook.com',
  'outllook.com': 'outlook.com',
  'outlookk.com': 'outlook.com',

  // Yahoo variations
  'yaho.com': 'yahoo.com',
  'yahooo.com': 'yahoo.com',
  'yhaoo.com': 'yahoo.com',
  'yhoo.com': 'yahoo.com',
  'yahoo.co': 'yahoo.com',
  'yahoo.con': 'yahoo.com',
  'yaoo.com': 'yahoo.com',
  'yahou.com': 'yahoo.com',
  'yaho.pl': 'yahoo.pl',

  // Polish domains - WP
  'wp.p': 'wp.pl',
  'wp.l': 'wp.pl',
  'wpp.pl': 'wp.pl',
  'w.pl': 'wp.pl',
  'wp.com': 'wp.pl',

  // Polish domains - Onet
  'onet.p': 'onet.pl',
  'onet.l': 'onet.pl',
  'onett.pl': 'onet.pl',
  'oneet.pl': 'onet.pl',
  'onet.com': 'onet.pl',
  'onnet.pl': 'onet.pl',
  'oner.pl': 'onet.pl',

  // Polish domains - Interia
  'interia.p': 'interia.pl',
  'interia.l': 'interia.pl',
  'inteira.pl': 'interia.pl',
  'interai.pl': 'interia.pl',
  'intria.pl': 'interia.pl',
  'interiaa.pl': 'interia.pl',
  'interia.com': 'interia.pl',

  // Polish domains - O2
  'o2.p': 'o2.pl',
  'o2.l': 'o2.pl',
  '02.pl': 'o2.pl',
  'o2.com': 'o2.pl',

  // Polish domains - Gazeta
  'gazeta.p': 'gazeta.pl',
  'gazeta.l': 'gazeta.pl',
  'gazzeta.pl': 'gazeta.pl',
  'gazetta.pl': 'gazeta.pl',
  'gazeta.com': 'gazeta.pl',

  // iCloud
  'icoud.com': 'icloud.com',
  'iclod.com': 'icloud.com',
  'icload.com': 'icloud.com',
  'icloud.co': 'icloud.com',
  'icloud.con': 'icloud.com',

  // ProtonMail
  'protonmal.com': 'protonmail.com',
  'protonmai.com': 'protonmail.com',
  'protonmail.co': 'protonmail.com',
  'protonmail.con': 'protonmail.com',
};

export interface EmailDomainError {
  suggestedDomain: string;
  suggestedEmail: string;
}

/**
 * Validator that checks for common email domain typos and suggests corrections
 */
export function emailDomainValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const email = control.value;

    if (!email || typeof email !== 'string') {
      return null;
    }

    const atIndex = email.lastIndexOf('@');
    if (atIndex === -1) {
      return null; // Let the built-in email validator handle this
    }

    const domain = email.substring(atIndex + 1).toLowerCase();
    const localPart = email.substring(0, atIndex);

    const correction = DOMAIN_CORRECTIONS[domain];
    if (correction) {
      return {
        emailDomainSuggestion: {
          suggestedDomain: correction,
          suggestedEmail: `${localPart}@${correction}`,
        } as EmailDomainError,
      };
    }

    return null;
  };
}

/**
 * Helper function to apply suggested email correction
 */
export function applySuggestedEmail(control: AbstractControl): void {
  const errors = control.errors;
  if (errors?.['emailDomainSuggestion']) {
    const suggestion = errors['emailDomainSuggestion'] as EmailDomainError;
    control.setValue(suggestion.suggestedEmail);
    control.updateValueAndValidity();
  }
}
