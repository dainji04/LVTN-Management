interface RegisterUser {
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    phoneNumber: string;
    password: string;
    gender: string;
}

export interface RegisterForm extends RegisterUser {
    day: number;
    month: number;
    year: number;
}

export interface RegisterRequest extends RegisterForm {
    birthDay: string;
}