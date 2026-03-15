interface RegisterUser {
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
    password: string;
    gender: string;
}

export interface RegisterForm extends RegisterUser {
    day: string;
    month: string;
    year: string;
}

export interface RegisterRequest extends RegisterForm {
    birthDay: string;
}