const elements = {
	NAME_INPUT: '#formChannelData input[formcontrolname="name"]',
	STATUS_SELECT: '#formChannelData mat-select[formcontrolname="status"]',
	EVENT_SLIDE: '#formChannelData mat-slide-toggle[formcontrolname="multiEvent"]',
	BUILD_SELECT: '#formChannelData mat-select[formcontrolname="build"]',
	LANGUAGES: {
		CHECKBOX: '.language-selector mat-checkbox',
		CHECKBOX_STAR: '.language-selector mat-icon',
	},
	MULTI_EVENT: {
		CHECKBOX: 'mat-checkbox[formcontrolname="multiEvent"]',
		CHECKBOX_LABEL: 'mat-checkbox[formcontrolname="multiEvent"] label'
	},
	CONTACT: {
		ENTITY_MANAGER_INPUT: '#formContactData input[formcontrolname="entityManager"]',
		ENTITY_OWNER_INPUT: '#formContactData input[formcontrolname="entityOwner"]',
		NAME_INPUT: '#formContactData input[formcontrolname="name"]',
		SURNAME_INPUT: '#formContactData input[formcontrolname="surname"]',
		POSITION_INPUT: '#formContactData input[formcontrolname="position"]',
		WEB_INPUT: '#formContactData input[formcontrolname="web"]',
		EMAIL_INPUT: '#formContactData input[formcontrolname="email"]',
		PHONE_INPUT: '#formContactData input[formcontrolname="phone"]'
    }
}
export default elements;