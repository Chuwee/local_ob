import { BuySeatFlow, MemberPeriods, MemberStep, NewMemberFlow } from '@admin-clients/cpanel-channels-member-external-data-access';

export const steps = {
    [MemberPeriods.buyNew + NewMemberFlow.payment]: [
        { number: 1, id: MemberStep.userData, deactivable: false },
        { child: true, id: MemberStep.conditions, deactivable: true, controlName: 'show_conditions' },
        { number: 2, id: MemberStep.newOrder, deactivable: false },
        { number: 3, id: MemberStep.newPayment, deactivable: false },
        { number: 4, id: MemberStep.thanks, deactivable: false }
    ],
    [MemberPeriods.buyNew + NewMemberFlow.autologin]: [
        { number: 1, id: MemberStep.userData, deactivable: false },
        { child: true, id: MemberStep.conditions, deactivable: true, controlName: 'show_conditions' },
        { number: 2, id: MemberStep.autologin, deactivable: false }
    ],
    [MemberPeriods.buyNew + NewMemberFlow.emailValidation]: [
        { number: 1, id: MemberStep.userData, deactivable: false },
        { child: true, id: MemberStep.conditions, deactivable: true, controlName: 'show_conditions' },
        { number: 2, id: MemberStep.email, deactivable: false },
        { number: 3, id: MemberStep.autologin, deactivable: false }
    ],
    [MemberPeriods.buy + BuySeatFlow.internal]: [
        { number: 1, id: MemberStep.info, deactivable: true },
        { number: 2, id: MemberStep.mode, deactivable: true, info: true },
        { number: 3, id: MemberStep.role, deactivable: true, info: true },
        { number: 4, id: MemberStep.locations, deactivable: false },
        { child: true, id: MemberStep.periodicities, deactivable: true, controlName: 'skip_periodicity_module', invert: true },
        { number: 5, id: MemberStep.userData, deactivable: false },
        { child: true, id: MemberStep.editUser, deactivable: true, controlName: 'show_update_partner_user' },
        { number: 6, id: MemberStep.payment, deactivable: false },
        { number: 7, id: MemberStep.thanks, deactivable: false }
    ],
    [MemberPeriods.buy + BuySeatFlow.external]: [
        { number: 1, id: MemberStep.newUserData, deactivable: false },
        { child: true, id: MemberStep.conditions, deactivable: true, controlName: 'show_conditions' },
        { number: 2, id: MemberStep.mode, deactivable: true, info: true },
        { number: 3, id: MemberStep.role, deactivable: true, info: true },
        { number: 4, id: MemberStep.locations, deactivable: false },
        { child: true, id: MemberStep.periodicities, deactivable: true, controlName: 'skip_periodicity_module', invert: true },
        { number: 5, id: MemberStep.userData, deactivable: false },
        { number: 6, id: MemberStep.payment, deactivable: false },
        { number: 7, id: MemberStep.thanks, deactivable: false }
    ],
    [MemberPeriods.renewal]: [
        { number: 1, id: MemberStep.info, deactivable: true },
        { number: 2, id: MemberStep.data, deactivable: false },
        { number: 3, id: MemberStep.userData, deactivable: false },
        { child: true, id: MemberStep.editUser, deactivable: true, controlName: 'show_update_partner_user' },
        { number: 4, id: MemberStep.payment, deactivable: false },
        { number: 5, id: MemberStep.thanks, deactivable: false }
    ],
    [MemberPeriods.change]: [
        { number: 1, id: MemberStep.info, deactivable: true },
        { number: 2, id: MemberStep.mode, deactivable: true, info: true },
        { number: 3, id: MemberStep.locations, deactivable: false },
        { child: true, id: MemberStep.periodicities, deactivable: true, controlName: 'skip_periodicity_module', invert: true },
        { number: 4, id: MemberStep.userData, deactivable: false },
        { child: true, id: MemberStep.editUser, deactivable: true, controlName: 'show_update_partner_user' },
        { number: 5, id: MemberStep.payment, deactivable: false },
        { number: 6, id: MemberStep.thanks, deactivable: false }
    ]
};
