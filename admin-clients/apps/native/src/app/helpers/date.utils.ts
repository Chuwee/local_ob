import moment from 'moment';

namespace DateUtils {
    export const getDaysOfWeekFromCurrentDate = (
    ): moment.Moment[] => {
        const currentDate = moment();

        return [
            currentDate.clone().day(0),
            currentDate.clone().day(1),
            currentDate.clone().day(2),
            currentDate.clone().day(3),
            currentDate.clone().day(4),
            currentDate.clone().day(5),
            currentDate.clone().day(6),
            currentDate.clone().day(7)
        ];
    };
}

export default DateUtils;
